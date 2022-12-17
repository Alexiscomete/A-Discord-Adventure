package io.github.alexiscomete.lapinousecond.view.manager

import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.contextFor
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.listener.interaction.SelectMenuChooseListener

class SelectMenuManager : SelectMenuChooseListener {

    override fun onSelectMenuChoose(p0: SelectMenuChooseEvent) {
        try {
            val player = getAccount(p0.selectMenuInteraction.user)
            contextFor(player)
                .apply(p0.selectMenuInteraction.customId, p0)
        } catch (e: Exception) {
            p0.selectMenuInteraction.createImmediateResponder()
                .setContent("Une erreur est survenue : " + e.message)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond()
            e.printStackTrace()
        }
    }
}