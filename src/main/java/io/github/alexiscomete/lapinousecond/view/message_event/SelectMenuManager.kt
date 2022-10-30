package io.github.alexiscomete.lapinousecond.view.message_event

import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.Players
import io.github.alexiscomete.lapinousecond.view.contextFor
import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.listener.interaction.SelectMenuChooseListener
import java.util.function.Consumer

class SelectMenuManager : SelectMenuChooseListener {

    override fun onSelectMenuChoose(p0: SelectMenuChooseEvent) {
        try {
            val player = getAccount(p0.selectMenuInteraction.user)
            contextFor(player)
                .apply(p0.selectMenuInteraction.customId, p0)
        } catch (e: Exception) {
            p0.selectMenuInteraction.createImmediateResponder()
                .setContent("Une erreur est survenue : " + e.message)
                .respond()
            e.printStackTrace()
        }
    }
}