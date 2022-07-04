package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.commands.CommandInServer
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.resources.WorkEnum
import io.github.alexiscomete.lapinousecond.roles.Role
import roles.RolesEnum
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.time.Instant
import java.util.*

class Work : CommandInServer(
    "Gagnez de l'argent et/ou des ressources",
    "work",
    "Utilisable régulièrement pour gagner un peut d'argent ou des ressources, c'est le moyen le plus simple d'en gagner. Les rôles représentent votre implication dans le Dibistan (-role), le work est pour tout le monde",
    "PLAY"
) {
    override fun executeC(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        val embedBuilder = EmbedBuilder()
            .setColor(Color.ORANGE)
            .setAuthor(messageCreateEvent.messageAuthor)
        val roles = StringBuilder()
        val optionalUser = messageCreateEvent.messageAuthor.asUser()
        val optionalServer = messageCreateEvent.server
        if (optionalUser.isPresent && optionalServer.isPresent) {
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
                        roles.append(role.name_).append(" : ").append(role.salary).append("\n")
                        find.currentCooldown = (System.currentTimeMillis() / 1000)
                        totalRoles += role.salary.toDouble()
                    } else {
                        roles.append(role.name_).append(" : ").append("Cooldown -> <t:")
                            .append(find.currentCooldown.toInt() + role.coolDownSize).append(":R>\n")
                    }
                } else {
                    roles.append(role.name_).append(" : ").append(role.salary).append("\n")
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