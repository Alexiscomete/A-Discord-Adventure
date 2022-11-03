package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction
import java.awt.Color

class Armurerie(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun getCompleteInfos(p: Context): MenuBuilder {
        return MenuBuilder(
            "Armurerie",
            "Bienvenue dans l'armurerie !",
            Color.DARK_GRAY,
            p
        )
    }

    override fun descriptionShort(): String {
        return "Armurerie"
    }

    override fun title(): String {
        return "Armurerie"
    }

    override fun configBuilding() {}
}