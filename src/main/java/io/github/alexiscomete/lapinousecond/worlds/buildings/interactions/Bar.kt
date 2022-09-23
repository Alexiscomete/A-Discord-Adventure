package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction
import java.awt.Color

class Bar(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun getCompleteInfos(p: Player): MenuBuilder {
        return MenuBuilder(
            "Bar",
            "Le bar est un endroit convivial où vous pouvez vous détendre et vous amuser.",
            Color.DARK_GRAY,
            p.id
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