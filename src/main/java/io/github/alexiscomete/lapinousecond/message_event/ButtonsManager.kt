package io.github.alexiscomete.lapinousecond.message_event

import org.javacord.api.event.interaction.MessageComponentCreateEvent
import org.javacord.api.listener.interaction.MessageComponentCreateListener
import java.util.function.Consumer

class ButtonsManager : MessageComponentCreateListener {
    private val hashButton = HashMap<Long, Consumer<MessageComponentCreateEvent>>()
    override fun onComponentCreate(messageComponentCreateEvent: MessageComponentCreateEvent) {
        if (hashButton.containsKey(messageComponentCreateEvent.messageComponentInteraction.customId.toLong())) {
            try {
                hashButton[messageComponentCreateEvent.messageComponentInteraction.customId.toLong()]!!.accept(
                    messageComponentCreateEvent
                )
            } catch (e: Exception) {
                if (messageComponentCreateEvent.messageComponentInteraction.channel.isPresent) {
                    messageComponentCreateEvent.messageComponentInteraction.channel.get()
                        .sendMessage("Une erreur est survenue : " + e.message)
                } else {
                    messageComponentCreateEvent.messageComponentInteraction.user.sendMessage(
                        """Une erreur est survenue : ${e.message}
    Impossible de répondre à votre message dans le channel donc ce message est envoyé en DM."""
                    )
                }
            }
        } else {
            messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater()
                .removeAllEmbeds()
                .removeAllComponents()
                .setContent("Il est impossible de répondre à cette demande, soit le bouton est invalide soit le bot a été redémarré (pas de mémoire à long terme pour les boutons)")
                .update()
        }
    }

    fun addButton(id: Long, eventConsumer: Consumer<MessageComponentCreateEvent>) {
        hashButton[id] = eventConsumer
    }
}