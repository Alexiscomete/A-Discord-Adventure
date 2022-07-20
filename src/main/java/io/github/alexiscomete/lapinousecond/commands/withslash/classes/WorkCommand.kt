package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.resources.WorkEnum
import io.github.alexiscomete.lapinousecond.roles.Role
import io.github.alexiscomete.lapinousecond.roles.RolesEnum
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color
import java.time.Instant
import java.util.*

// on défini la classe principale de la commande
class WorkCommandBase : Command(
    "work",
    "Permet de récupérer de façon régulière des resources et des ${Resource.RABBIT_COIN.name_}",
    "work [roles/all/resources]",
    inDms = false,
    subCommands = listOf()
)

// la sous commande "roles"
class WorkCommandRoles :
    SubCommand(
        "roles",
        "Permet de récupérer uniquement les ${Resource.RABBIT_COIN.name_} des roles lié au serveur où vous êtes"
    ),
    ExecutableWithArguments {
    override val botPerms: Array<String>?
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        TODO("Not yet implemented")
    }
}

// la sous commande "all"
class WorkCommandAll :
    SubCommand(
        "all",
        "Permet de récupérer toutes les ${Resource.RABBIT_COIN.name_} et resources"
    ),
    ExecutableWithArguments {
    override val botPerms: Array<String>?
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player = getAccount(slashCommand)
        val serverBot = getCurrentServerBot(slashCommand)
        val embedBuilder = EmbedBuilder()
            .setColor(Color.ORANGE)
            .setAuthor(slashCommand.user)
        val roles = StringBuilder()
        val user = slashCommand.user
        val optionalServer = messageCreateEvent.server
        if (true && optionalServer.isPresent) {
            var totalRoles = 0.0
            val user = optionalUser.get()
            val server = optionalServer.get()
            val rolesArrayList = RolesEnum.getRoles(user, server)
            for (role in rolesArrayList) {
                var find: Role? = null
                for (r in p.roles) {
                    if (r.role == role) {
                        find = r
                        break
                    }
                }
                if (find != null) {
                    if (find.isReady) {
                        roles.append(role.name_).append(" : ").append(role.salary).append(" ${Resource.RABBIT_COIN.name_} \n")
                        find.currentCooldown = (System.currentTimeMillis() / 1000)
                        totalRoles += role.salary.toDouble()
                    } else {
                        roles.append(role.name_).append(" : ").append("Cooldown -> <t:")
                            .append(find.currentCooldown.toInt() + role.coolDownSize).append(":R>\n")
                    }
                } else {
                    roles.append(role.name_).append(" : ").append(role.salary).append(" ${Resource.RABBIT_COIN.name_} \n")
                    totalRoles += role.salary.toDouble()
                    val r = Role(role)
                    r.currentCooldown = (System.currentTimeMillis() / 1000)
                    p.addRole(r)
                }
            }
            p["bal"] = (p["bal"].toDouble() + totalRoles).toString()
        } else {
            roles.append("Vous n'êtes pas sur un serveur")
        }
        if (roles.isNotEmpty()) embedBuilder.addField("Roles", roles.toString())
        if (System.currentTimeMillis() - p.workTime > 200000) {
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
                p["bal"] = (p["bal"].toDouble() + r).toString()
            } else {
                var resourceManager = p.resourceManagers[woAnswer.resource]
                if (resourceManager == null) {
                    resourceManager = ResourceManager(woAnswer.resource!!, r)
                    p.resourceManagers[woAnswer.resource!!] = resourceManager
                } else {
                    resourceManager.quantity = resourceManager.quantity + r
                }
                p.updateResources()
            }
            p.updateWorkTime()
            if (p["tuto"].toInt() == 3) {
                messageCreateEvent.message.reply("La récompense peut varier d'un work à un autre. Utilisez inv ...")
                p["tuto"] = "4"
            }
        } else {
            embedBuilder.addField(
                "Work",
                "Cooldown ! Temps entre 2 work : 200s, temps écoulé : " + (System.currentTimeMillis() - p.workTime) / 1000 + "s. Temps avant le prochain : <t:" + (Instant.now().epochSecond + 200 - (System.currentTimeMillis() - p.workTime) / 1000) + ":R>"
            )
        }
        messageCreateEvent.message.reply(embedBuilder)
    }
}

// la sous commande "resources"
class WorkCommandResources :
    SubCommand(
        "resources",
        "Permet de récupérer uniquement les resources"
    ),
    ExecutableWithArguments {
    override val botPerms: Array<String>?
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        TODO("Not yet implemented")
    }
}

