package io.github.alexiscomete.lapinousecond.view.discord.manager

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import io.github.alexiscomete.lapinousecond.entity.xp.LevelRewards
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

const val XP_FOR_MESSAGE = 0.2
const val XP_FOR_MESSAGE_COOLDOWN_MILLIS = 15_000

fun getLevelEmbed(pair: LevelRewards): EmbedBuilder {
    val embedBuilder = EmbedBuilder()
        .setTitle("Tu es passé.e du niveau " + (pair.level - 1) + " au niveau " + pair.level + " !")
        .setFooter("Pour désactiver ce message, interdisez au bot de parler dans ce salon, ou utilisez la commande /settings")
    val rewards = pair.toStrings()
    if (rewards.isNotEmpty()) {
        embedBuilder.setDescription(rewards.joinToString("\n"))
    }
    return embedBuilder
}

class MessagesManager : MessageCreateListener {
    override fun onMessageCreate(messageCreateEvent: MessageCreateEvent) {
        if (messageCreateEvent.messageAuthor.isUser) {
            try {
                val playerManager = PlayerManager[messageCreateEvent.messageAuthor.id]
                if (playerManager.lastLevelUpdate + XP_FOR_MESSAGE_COOLDOWN_MILLIS < System.currentTimeMillis()) {
                    val pair = playerManager.level.addXpWithReward(XP_FOR_MESSAGE)
                    playerManager.lastLevelUpdate = System.currentTimeMillis()
                    if (pair != null && playerManager.playerData["notif"] != "d") {
                        messageCreateEvent.message.reply(getLevelEmbed(pair))
                    }
                }
            } catch (_: Exception) {

            }
        }
    }
}
