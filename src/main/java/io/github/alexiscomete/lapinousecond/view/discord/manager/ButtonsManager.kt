package io.github.alexiscomete.lapinousecond.view.discord.manager

import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.listener.interaction.ButtonClickListener

class ButtonsManager : ButtonClickListener {
    override fun onButtonClick(p0: ButtonClickEvent) {
        try {
            val player = getAccount(p0.buttonInteraction.user)
            contextFor(player)
                .apply(p0.buttonInteraction.customId, p0)
        } catch (e: Exception) {
            p0.buttonInteraction.createImmediateResponder()
                .setContent("Une erreur est survenue : " + e.message)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond()
            e.printStackTrace()
        }
    }
}