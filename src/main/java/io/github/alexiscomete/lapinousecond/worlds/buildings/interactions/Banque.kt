package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions

import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction
import java.awt.Color

class Banque(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun getCompleteInfos(p: Context): MenuBuilder {
        return MenuBuilder(
            "Banque",
            "Vous pouvez déposer ou retirer de l'argent de votre compte bancaire ici.",
            Color.DARK_GRAY,
            p
        )
    }

    override fun descriptionShort(): String {
        return "Banque"
    }

    override fun title(): String {
        return "Banque"
    }

    override fun configBuilding() {}
}