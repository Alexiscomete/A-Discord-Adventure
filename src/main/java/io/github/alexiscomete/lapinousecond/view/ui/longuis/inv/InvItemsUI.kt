package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.entity.items.Item
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI

class InvItemsUI(playerUI: PlayerUI) : EmbedPagesWithInteractions<Item>(
    run {
        //TODO
        return@run arrayListOf<Item>()
    },
    { i: Int, i1: Int, items: ArrayList<Item> ->
        //TODO
        listOf(Pair("Aucun item", "Aucun item"))
    },
    { u: Item, player: PlayerUI ->
        null
    },
    "https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png",
    null,
    "Inventaire de vos items",
    "Le système d'items n'est pas encore implémenté. Les items sont utilisables et ne fusionnent pas dans l'inventaire contrairement aux ressources.",
    playerUI
) {
    override fun addComponents() {
        val bu = listOf(
            SimpleInteractionUICustomUI(
                "inv_resources",
                "Ressources",
                "Ouvrir l'inventaire des ressources",
                InteractionStyle.NORMAL,
                {
                    currentUI.setLongCustomUI(InvResourcesUI(currentUI))
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
                    currentUI.setLongCustomUI(InvInfosUI(currentUI))
                    return@SimpleInteractionUICustomUI null
                },
                null
            ),
            SimpleInteractionUICustomUI(
                "inv_effects",
                "Effets",
                "Ouvrir la liste des effets en cours",
                InteractionStyle.NORMAL,
                {
                    currentUI.setLongCustomUI(InvEffectsUI(currentUI))
                    return@SimpleInteractionUICustomUI null
                },
                null
            )
        )
        if (buttons.isEmpty()) {
            setInteractionUICustomUIs(
                listOf(
                    components,
                    bu
                )
            )
        } else {
            setInteractionUICustomUIs(
                listOf(
                    buttons,
                    components,
                    bu
                )
            )
        }
    }
}