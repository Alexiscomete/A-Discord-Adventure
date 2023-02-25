package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.ui.old.MenuBuilder
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction
import java.awt.Color

class Marche(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun descriptionShort(): String {
        return "Marché"
    }

    override fun title(): String {
        return "Marché"
    }

    override fun getCompleteInfos(p: Context): MenuBuilder {
        return MenuBuilder("Marché", "Bienvenue dans le marché !", Color.DARK_GRAY,p)
    }

    override fun configBuilding() {}
}