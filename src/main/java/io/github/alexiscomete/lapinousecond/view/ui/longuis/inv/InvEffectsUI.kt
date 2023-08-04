package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.entity.effects.EffectEnum
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPages
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI

class InvEffectsUI(playerUI: PlayerUI) : EmbedPages<InvEffectUIPart>(
    null,
    null,
    "Inventaire des effets",
    "Les effets peuvent exister en plusieurs exemplaires car les niveaux sont cumulables.",
    "Chaque catégorie correspond à un effet avec à chaque fois le niveau total et les différentes pairs de temps et de niveau.",
    run {
        val effects = playerUI.getPlayerManager().effectsManager.getEffectsCopy()
        val dico = HashMap<EffectEnum, InvEffectUIPart>()
        effects.forEach { effect ->
            val part = dico.getOrPut(effect.type) { InvEffectUIPart(effect.type) }
            part.add(effect)
        }
        arrayListOf(*dico.values.toTypedArray())
    },
    { i: Int, i1: Int, invEffectUIParts: ArrayList<InvEffectUIPart> ->
        invEffectUIParts.subList(i, i1).map { it.getPair() }
    },
    playerUI
) {
    override fun addComponents() {
        setInteractionUICustomUIs(listOf(components, listOf(
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
                "inv_items",
                "Items",
                "Ouvrir l'inventaire des items",
                InteractionStyle.NORMAL,
                {
                    currentUI.setLongCustomUI(InvItemsUI(currentUI))
                    null
                },
                null
            )
        )))
    }
}