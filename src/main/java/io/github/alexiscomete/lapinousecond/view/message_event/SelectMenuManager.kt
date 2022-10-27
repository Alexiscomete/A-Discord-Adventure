package io.github.alexiscomete.lapinousecond.view.message_event

import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.listener.interaction.SelectMenuChooseListener
import java.util.function.Consumer

class SelectMenuManager : SelectMenuChooseListener {
    private val hash = HashMap<Long, Consumer<SelectMenuChooseEvent>>()

    fun add(id: Long, consumer: Consumer<SelectMenuChooseEvent>) {
        hash[id] = consumer
    }

    override fun onSelectMenuChoose(p0: SelectMenuChooseEvent) {
        if (hash.containsKey(p0.selectMenuInteraction.customId.toLong())) {
            try {
                hash[p0.selectMenuInteraction.customId.toLong()]!!.accept(p0)
            } catch (e: Exception) {
                p0.selectMenuInteraction.createImmediateResponder()
                    .setContent("Une erreur est survenue : " + e.message)
                    .respond()
                e.printStackTrace()
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