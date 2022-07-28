package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.resources.WorkEnum
import io.github.alexiscomete.lapinousecond.roles.Role
import io.github.alexiscomete.lapinousecond.roles.RolesEnum
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder
import java.awt.Color
import java.time.Instant
import java.util.*

fun setWork(
    player: Player,
    embedBuilder: EmbedBuilder,
    response: InteractionImmediateResponseBuilder
) {
    if (System.currentTimeMillis() - player.workTime > 200000) {
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
        if (player["tuto"].toInt() == 3) {
            response.setContent("La récompense peut varier d'un work à un autre. Utilisez inv ...")
            player["tuto"] = "4"
        }
    } else {
        embedBuilder.addField(
            "Work",
            "Cooldown ! Temps entre 2 work : 200s, temps écoulé : " + (System.currentTimeMillis() - player.workTime) / 1000 + "s. Temps avant le prochain : <t:" + (Instant.now().epochSecond + 200 - (System.currentTimeMillis() - player.workTime) / 1000) + ":R>"
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
                    roles.append(role.name_).append(" : ").append(role.salary)
                        .append(" ${Resource.RABBIT_COIN.name_} \n")
                    find.currentCooldown = (System.currentTimeMillis() / 1000)
                    totalRoles += role.salary.toDouble()
                } else {
                    roles.append(role.name_).append(" : ").append("Cooldown -> <t:")
                        .append(find.currentCooldown.toInt() + role.coolDownSize).append(":R>\n")
                }
            } else {
                roles.append(role.name_).append(" : ").append(role.salary)
                    .append(" ${Resource.RABBIT_COIN.name_} \n")
                totalRoles += role.salary.toDouble()
                val r = Role(role)
                r.currentCooldown = (System.currentTimeMillis() / 1000)
                player.addRole(r)
            }
        }
        player["bal"] = (player["bal"].toDouble() + totalRoles).toString()
    } else {
        roles.append("Vous n'êtes pas sur un serveur")
    }
    if (roles.isNotEmpty()) embedBuilder.addField("Roles", roles.toString())
}

// on défini la classe principale de la commande
class WorkCommandBase : Command(
    "work",
    "Récupérer des resources et des rabbit coins",
    "work [roles/all/resources]",
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
    override val botPerms: Array<String>?
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
    override val botPerms: Array<String>?
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player = getAccount(slashCommand)
        getCurrentServerBot(slashCommand)
        val embedBuilder = EmbedBuilder()
            .setColor(Color.ORANGE)
            .setAuthor(slashCommand.user)
        val response = slashCommand.createImmediateResponder()
        setRoles(slashCommand, player, embedBuilder)
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
    override val botPerms: Array<String>?
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

