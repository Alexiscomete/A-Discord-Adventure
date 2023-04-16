package io.github.alexiscomete.lapinousecond.view.discord.manager

import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.contextFor
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.listener.interaction.ModalSubmitListener

class ModalManager : ModalSubmitListener {
    override fun onModalSubmit(p0: ModalSubmitEvent) {
        try {
            val player = getAccount(p0.modalInteraction.user)
            contextFor(player)
                .apply(p0.modalInteraction.customId, p0)
        } catch (e: Exception) {
            p0.modalInteraction.createImmediateResponder()
                .setContent("Une erreur est survenue : " + e.message)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond()
            e.printStackTrace()
        }
    }
}