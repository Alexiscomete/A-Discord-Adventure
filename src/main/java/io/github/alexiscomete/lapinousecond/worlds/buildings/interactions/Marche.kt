package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.message_event.MenuBuilder
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

    override fun getCompleteInfos(p: Player): MenuBuilder {
        return MenuBuilder("Marché", "Bienvenue dans le marché !", Color.DARK_GRAY,p.id)
    }

    override fun configBuilding() {}
}