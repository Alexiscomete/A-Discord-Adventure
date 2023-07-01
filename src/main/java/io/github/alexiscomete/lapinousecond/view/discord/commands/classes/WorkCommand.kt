package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.data.TutoSteps
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.WorkEnum
import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.entity.roles.Role
import io.github.alexiscomete.lapinousecond.entity.roles.RolesEnum
import io.github.alexiscomete.lapinousecond.view.discord.commands.*
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder
import java.awt.Color
import java.time.Instant
import java.util.*

const val WORK_COOLDOWN_MILLIS = 200_000
const val WORK_COOLDOWN_SECONDS = WORK_COOLDOWN_MILLIS / 1000

fun setWork(
    player: Player,
    embedBuilder: EmbedBuilder,
    response: InteractionImmediateResponseBuilder
) {
    if (System.currentTimeMillis() - player.workTime > WORK_COOLDOWN_MILLIS) {
        val wo = WorkEnum.values()
        val random = Random()
        var total = 0
        for (w in wo) {
            total += w.coef
        }
        var woAnswer = wo[0]
        var ran = random.nextInt(total)
        for (w in wo) {
            ran -= w.coef
            if (ran <= 0) {
                woAnswer = w
                break
            }
        }
        val strings = woAnswer.answer.split(" rc ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val r = Random().nextInt(woAnswer.max - woAnswer.min) + woAnswer.min
        val answer: String = if (strings.size > 1) {
            strings[0] + " " + r + " " + strings[1]
        } else {
            strings[0]
        }
        embedBuilder.addField("Work", answer)
        if (woAnswer.resource == null) {
            player["bal"] = (player["bal"].toDouble() + r).toString()
        } else {
            var resourceManager = player.resourceManagers[woAnswer.resource]
            if (resourceManager == null) {
                resourceManager = ResourceManager(woAnswer.resource!!, r)
                player.resourceManagers[woAnswer.resource!!] = resourceManager
            } else {
                resourceManager.quantity = resourceManager.quantity + r
            }
            player.updateResources()
        }
        player.updateWorkTime()
        player.level.addXp(0.5)
        if (player["tuto"] == TutoSteps.STEP_WORK.number) {
            response.setContent("> (Aurimezi) : Bon tu as déjà plus de trucs. Maintenant on va utiliser ma fonctionnalité de magasin pour échanger ce que tu as trouvé. Bon qu'est ce qu'on a ramassé ...\n\nUtilisez à nouveau la commande d'inventaire")
            player["tuto"] = TutoSteps.STEP_WORK.nextStepNum
            player.level.addXp(1.0)
        }
    } else {
        embedBuilder.addField(
            "Work",
            "Cooldown ! Temps entre 2 work : ${WORK_COOLDOWN_SECONDS}s, temps écoulé : " + (System.currentTimeMillis() - player.workTime) / 1000 + "s. Temps avant le prochain : <t:" + (Instant.now().epochSecond + WORK_COOLDOWN_SECONDS - (System.currentTimeMillis() - player.workTime) / 1000) + ":R>"
        )
    }
}

private fun setRoles(
    slashCommand: SlashCommandInteraction,
    player: Player,
    embedBuilder: EmbedBuilder
) {
    val roles = StringBuilder()
    val user = slashCommand.user
    val optionalServer = slashCommand.server
    if (optionalServer.isPresent) {
        var totalRoles = 0.0
        val server = optionalServer.get()
        val rolesArrayList = RolesEnum.getRoles(user, server)
        for (role in rolesArrayList) {
            var find: Role? = null
            for (r in player.roles) {
                if (r.role == role) {
                    find = r
                    break
                }
            }
            if (find != null) {
                if (find.isReady) {
                    roles.append(role.displayName).append(" : ").append(role.salary)
                        .append(" ${Resource.RABBIT_COIN.show} \n")
                    find.currentCooldown = (System.currentTimeMillis() / 1000)
                    totalRoles += role.salary.toDouble()
                } else {
                    roles.append(role.displayName).append(" : ").append("Cooldown -> <t:")
                        .append(find.currentCooldown.toInt() + role.coolDownSize).append(":R>\n")
                }
            } else {
                roles.append(role.displayName).append(" : ").append(role.salary)
                    .append(" ${Resource.RABBIT_COIN.show} \n")
                totalRoles += role.salary.toDouble()
                val r = Role(role)
                r.currentCooldown = (System.currentTimeMillis() / 1000)
                player.addRole(r)
            }
        }
        player["bal"] = (player["bal"].toDouble() + totalRoles).toString()
        player.level.addXp(0.5)
    } else {
        roles.append("Vous n'êtes pas sur un serveur")
    }
    if (roles.isNotEmpty()) embedBuilder.addField("Roles", roles.toString())
}

// on défini la classe principale de la commande
class WorkCommandBase : Command(
    "work",
    "Récupérer des resources et des rabbit coins",
    inDms = false,
    subCommands = listOf(WorkCommandRoles(), WorkCommandAll(), WorkCommandResources())
)

// la sous commande "roles"
class WorkCommandRoles :
    SubCommand(
        "roles",
        "Récupérer les rabbit coins des roles lié au serveur où vous êtes"
    ),
    ExecutableWithArguments {

    override val fullName: String
        get() = "work roles"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player = getAccount(slashCommand)
        getCurrentServerBot(slashCommand)
        val embedBuilder = EmbedBuilder()
            .setColor(Color.ORANGE)
            .setAuthor(slashCommand.user)
        val response = slashCommand.createImmediateResponder()
        setRoles(slashCommand, player, embedBuilder)
        response
            .addEmbed(embedBuilder)
            .respond()
    }
}

// la sous commande "all"
class WorkCommandAll :
    SubCommand(
        "all",
        "Récupérer les rabbit coins et resources"
    ),
    ExecutableWithArguments {
    override val fullName: String
        get() = "work all"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player = getAccount(slashCommand)
        val embedBuilder = EmbedBuilder()
            .setColor(Color.ORANGE)
            .setAuthor(slashCommand.user)
        val response = slashCommand.createImmediateResponder()
        try {
            getCurrentServerBot(slashCommand)
            setRoles(slashCommand, player, embedBuilder)
        } catch (e: WrongServerException) {
            embedBuilder.setFooter("Vous n'êtes pas sur le bon serveur discord donc votre commande a été redirigée vers la commande `/work resources`")
        }
        setWork(player, embedBuilder, response)
        response
            .addEmbed(embedBuilder)
            .respond()
    }
}

// la sous commande "resources"
class WorkCommandResources :
    SubCommand(
        "resources",
        "Récupérer uniquement les resources"
    ),
    ExecutableWithArguments {
    override val fullName: String
        get() = "work resources"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player = getAccount(slashCommand)
        val embedBuilder = EmbedBuilder()
            .setColor(Color.ORANGE)
            .setAuthor(slashCommand.user)
        val response = slashCommand.createImmediateResponder()
        setWork(player, embedBuilder, response)
        response
            .addEmbed(embedBuilder)
            .respond()
    }
}

