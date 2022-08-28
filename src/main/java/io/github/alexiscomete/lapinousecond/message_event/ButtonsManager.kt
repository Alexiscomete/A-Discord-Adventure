package io.github.alexiscomete.lapinousecond.message_event

import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.MessageComponentCreateEvent
import org.javacord.api.listener.interaction.ButtonClickListener
import org.javacord.api.listener.interaction.MessageComponentCreateListener
import java.util.function.Consumer

class ButtonsManager : ButtonClickListener {
    private val hashButton = HashMap<Long, Consumer<ButtonClickEvent>>()

    fun addButton(id: Long, eventConsumer: Consumer<ButtonClickEvent>) {
        hashButton[id] = eventConsumer
    }

    override fun onButtonClick(p0: ButtonClickEvent) {
        if (hashButton.containsKey(p0.buttonInteraction.customId.toLong())) {
            try {
                hashButton[p0.buttonInteraction.customId.toLong()]!!.accept(
                    p0
                )
            } catch (e: Exception) {
                p0.buttonInteraction.createImmediateResponder()
                    .setContent("Une erreur est survenue : " + e.message)
                    .respond()
                e.printStackTrace()
            }
        } else {
            p0.buttonInteraction.createOriginalMessageUpdater().removeAllEmbeds().removeAllComponents()
                .setContent("Il est impossible de répondre à cette demande, soit le bouton est invalide soit le bot a été redémarré (pas de mémoire à long terme pour les boutons)")
                .update()
        }
    }
}