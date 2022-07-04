package message_event

import org.javacord.api.event.interaction.MessageComponentCreateEvent
import org.javacord.api.listener.interaction.MessageComponentCreateListener
import java.util.function.Consumer

class ButtonsManager : MessageComponentCreateListener {
    private val hashMap = HashMap<Long, HashMap<String, Consumer<MessageComponentCreateEvent>>>()
    private val hashButton = HashMap<Long, Consumer<MessageComponentCreateEvent>>()
    override fun onComponentCreate(messageComponentCreateEvent: MessageComponentCreateEvent) {
        if (hashMap.containsKey(messageComponentCreateEvent.messageComponentInteraction.message.id)) {
            val h = hashMap[messageComponentCreateEvent.messageComponentInteraction.message.id]!!
            if (h.containsKey(messageComponentCreateEvent.messageComponentInteraction.customId)) {
                try {
                    h[messageComponentCreateEvent.messageComponentInteraction.customId]!!.accept(
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
                    .removeAllComponents()
                    .removeAllEmbeds()
                    .setContent("Hum ... étrange, ce bouton semble ne pas exister")
                    .update()
            }
        } else if (hashButton.containsKey(messageComponentCreateEvent.messageComponentInteraction.customId.toLong())) {
            hashButton[messageComponentCreateEvent.messageComponentInteraction.customId.toLong()]!!.accept(
                messageComponentCreateEvent
            )
        } else {
            messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater().removeAllEmbeds()
                .removeAllComponents()
                .setContent("Il est impossible de répondre à cette demande, soit le bouton est invalide soit le bot a été redémarré (pas de mémoire à long terme pour les boutons)")
                .update()
        }
    }

    fun addButton(id: Long, eventConsumer: Consumer<MessageComponentCreateEvent>) {
        hashButton[id] = eventConsumer
    }

    fun addMessage(id: Long, hashMap: HashMap<String, Consumer<MessageComponentCreateEvent>>) {
        this.hashMap[id] = hashMap
    }
}