package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI

class InvItemsUI(playerUI: PlayerUI) : EmbedPagesWithInteractions<Item>(
    run {
        playerUI.getPlayerManager().ownerManager.getAllItems()
    },
    { i: Int, i1: Int, items: ArrayList<Item> ->
        val pairs = items.subList(i, i1).map { Pair("${it.name} (${it.id})", it.description) }
        pairs.ifEmpty { listOf(Pair("Aucun item", "Revenez plus tard")) }
    },
    { _: Item, _: PlayerUI ->
        null
    },
    INV_IMAGE_URL,
    null,
    "Inventaire de vos items",
    "Les items sont utilisables et ne fusionnent pas dans l'inventaire contrairement aux ressources.",
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
                    null
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
                    null
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
                    null
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