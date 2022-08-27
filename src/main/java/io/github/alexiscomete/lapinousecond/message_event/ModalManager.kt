package io.github.alexiscomete.lapinousecond.message_event

import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.listener.interaction.ModalSubmitListener
import java.util.function.Consumer

class ModalManager : ModalSubmitListener {
    val hash = HashMap<Long, Consumer<ModalSubmitEvent>>()

    fun add(id: Long, consumer: Consumer<ModalSubmitEvent>) {
        hash[id] = consumer
    }

    override fun onModalSubmit(p0: ModalSubmitEvent) {
        if (hash.containsKey(p0.modalInteraction.id)) {
            try {
                hash[p0.modalInteraction.id]!!.accept(p0)
            } catch (e: Exception) {
                if (p0.modalInteraction.channel.isPresent) {
                    p0.modalInteraction.channel.get()
                        .sendMessage("Une erreur est survenue : " + e.message)
                } else {
                    p0.modalInteraction.user.sendMessage(
                        """Une erreur est survenue : ${e.message}
    Impossible de répondre à votre message dans le channel donc ce message est envoyé en DM."""
                    )
                }
            }
        } else {
            p0.modalInteraction.createImmediateResponder()
                .removeAllEmbeds()
                .removeAllComponents()
                .setContent("Il est impossible de répondre à cette demande, soit le modal est invalide soit le bot a été redémarré (pas de mémoire à long terme pour les boutons)")
                .respond()
        }
    }
}