package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction

class Casino(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun getCompleteInfos(ui: PlayerUI): LongCustomUI {
        return MenuBuilderUI(
            "Casino",
            "Bienvenue dans le casino !",
            ui
        )
    }

    override fun descriptionShort(): String {
        return "Casino"
    }

    override fun title(): String {
        return "Casino"
    }

    override fun configBuilding() {}
}