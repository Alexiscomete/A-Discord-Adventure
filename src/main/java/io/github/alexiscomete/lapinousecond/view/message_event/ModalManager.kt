package io.github.alexiscomete.lapinousecond.view.message_event

import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.listener.interaction.ModalSubmitListener
import java.util.function.Consumer

class ModalManager : ModalSubmitListener {
    val hash = HashMap<Long, Consumer<ModalSubmitEvent>>()

    fun add(id: Long, consumer: Consumer<ModalSubmitEvent>) {
        hash[id] = consumer
    }

    override fun onModalSubmit(p0: ModalSubmitEvent) {
        if (hash.containsKey(p0.modalInteraction.customId.toLong())) {
            try {
                hash[p0.modalInteraction.customId.toLong()]!!.accept(p0)
            } catch (e: Exception) {
                p0.modalInteraction.createImmediateResponder()
                    .setContent("Une erreur est survenue : " + e.message)
                    .respond()
                e.printStackTrace()
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