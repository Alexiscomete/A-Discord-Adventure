package io.github.alexiscomete.lapinousecond.view.discord.manager

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

const val XP_FOR_MESSAGE = 0.2
const val XP_FOR_MESSAGE_COOLDOWN_MILLIS = 15_000

class MessagesManager : MessageCreateListener {
    override fun onMessageCreate(messageCreateEvent: MessageCreateEvent) {
        if (messageCreateEvent.messageAuthor.isUser) {
            try {
                val player = PlayerManager[messageCreateEvent.messageAuthor.id].playerData
                if (player.lastLevelUpdate + XP_FOR_MESSAGE_COOLDOWN_MILLIS < System.currentTimeMillis()) {
                    val pair = player.level.addXp(XP_FOR_MESSAGE)
                    player.lastLevelUpdate = System.currentTimeMillis()
                    if (pair != null) {
                        messageCreateEvent.message.reply("Tu es passé.e du niveau " + pair.first + " au niveau " + pair.second + " ! *Pour désactiver ce message, interdisez au bot de parler dans ce salon*")
                    }
                }
            } catch (_: Exception) {

            }
        }
    }
}