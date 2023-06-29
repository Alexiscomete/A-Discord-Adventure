package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction

class Bar(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun getCompleteInfos(ui: PlayerUI): LongCustomUI {
        return MenuBuilderUI(
            "Bar",
            "Le bar est un endroit convivial où vous pouvez vous détendre et vous amuser.",
            ui
        )
    }

    override fun descriptionShort(): String {
        return "Le bar est un endroit convivial où vous pouvez vous détendre et vous amuser."
    }

    override fun title(): String {
        return "Bar"
    }

    override fun configBuilding() {}
}