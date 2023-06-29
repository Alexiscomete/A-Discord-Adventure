package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction

class ArretBus(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun getCompleteInfos(ui: PlayerUI): LongCustomUI {
        return MenuBuilderUI(
            "Arrêt de bus",
            "Cet arrêt de bus est vide, il n'y a personne pour l'instant.",
            ui
        )
    }

    override fun descriptionShort(): String {
        return "Arrêt de bus"
    }

    override fun title(): String {
        return "Arrêt de bus"
    }

    override fun configBuilding() {}
}