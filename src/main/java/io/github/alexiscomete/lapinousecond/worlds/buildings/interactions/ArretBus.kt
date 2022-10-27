package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction
import java.awt.Color

class ArretBus(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun getCompleteInfos(p: Player): MenuBuilder {
        return MenuBuilder(
            "Arrêt de bus",
            "Cet arrêt de bus est vide, il n'y a personne pour l'instant.",
            Color(0, 0, 0),
            p.id
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