package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPages
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.StaticUI
import java.awt.image.BufferedImage

class InvEffectsUI(playerUI: PlayerUI) : EmbedPages<InvEffectUIPart>(
    null,
    null,
    "Inventaire des effets",
    "Les effets peuvent exister en plusieurs exemplaires car les niveaux sont cumulables.",
    "Chaque catégorie correspond à un effet avec à chaque fois le niveau total et les différentes pairs de temps et de niveau.",
    run {
        return@run ArrayList<InvEffectUIPart>()
    },
    {

    },
    playerUI
) {

    override fun getFields(): List<Pair<String, String>>? {
        // TODO
        return null
    }
}