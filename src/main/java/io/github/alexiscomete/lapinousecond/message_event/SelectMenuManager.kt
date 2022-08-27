package io.github.alexiscomete.lapinousecond.message_event

import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.listener.interaction.SelectMenuChooseListener
import java.util.function.Consumer

class SelectMenuManager : SelectMenuChooseListener {
    val hash = HashMap<Long, Consumer<SelectMenuChooseEvent>>()

    fun add(id: Long, consumer: Consumer<SelectMenuChooseEvent>) {
        hash[id] = consumer
    }

    override fun onSelectMenuChoose(p0: SelectMenuChooseEvent) {
        if (hash.containsKey(p0.selectMenuInteraction.id)) {
            try {
                hash[p0.selectMenuInteraction.id]!!.accept(p0)
            } catch (e: Exception) {
                if (p0.selectMenuInteraction.channel.isPresent) {
                    p0.selectMenuInteraction.channel.get()
                        .sendMessage("Une erreur est survenue : " + e.message)
                } else {
                    p0.selectMenuInteraction.user.sendMessage(
                        """Une erreur est survenue : ${e.message}
    Impossible de répondre à votre message dans le channel donc ce message est envoyé en DM."""
                    )
                }
            }
        } else {
            p0.selectMenuInteraction.createImmediateResponder()
                .removeAllEmbeds()
                .removeAllComponents()
                .setContent("Il est impossible de répondre à cette demande, soit le menu de sélection est invalide soit le bot a été redémarré (pas de mémoire à long terme pour les boutons)")
                .respond()
        }
    }
}