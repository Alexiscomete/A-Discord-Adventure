package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.StaticUI
import java.awt.image.BufferedImage

class InvItemsUI(playerUI: PlayerUI) : StaticUI(
    interactionUICustomUILists = listOf(
        listOf(
            SimpleInteractionUICustomUI(
                "inv_resources",
                "Ressources",
                "Ouvrir l'inventaire des ressources",
                InteractionStyle.NORMAL,
                {
                    playerUI.setLongCustomUI(InvResourcesUI(playerUI));
                    return@SimpleInteractionUICustomUI null
                },
                null
            ),
            SimpleInteractionUICustomUI(
                "inv_infos",
                "Informations",
                "Ouvrir l'inventaire des informations",
                InteractionStyle.NORMAL,
                {
                    playerUI.setLongCustomUI(InvInfosUI(playerUI));
                    return@SimpleInteractionUICustomUI null
                },
                null
            )
        )
    ),
    playerUI
) {
    override fun getTitle(): String {
        return "Inventaire de vos items"
    }

    override fun getDescription(): String {
        // TODO
        return "Le système d'items n'est pas encore implémenté"
    }

    override fun getFields(): List<Pair<String, String>>? {
        // TODO
        return null
    }

    override fun getLinkedImage(): String {
        return "https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png"
    }

    override fun getBufferedImage(): BufferedImage? {
        return null
    }

    override fun getUnderString(): String {
        return "Les items sont utilisables et ne fusionnent pas dans l'inventaire contrairement aux ressources"
    }
}