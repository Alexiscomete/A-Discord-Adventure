package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction
import java.awt.Color

class Bibliotheque(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun getCompleteInfos(p: Player): MenuBuilder {
        return MenuBuilder(
            "Bibliothèque",
            "Bienvenue dans la bibliothèque de l'Université de Lapinou !",
            Color.DARK_GRAY,
            p.id
        )
    }

    override fun descriptionShort(): String {
        return "Bibliothèque"
    }

    override fun title(): String {
        return "Bibliothèque"
    }

    override fun configBuilding() {}
}