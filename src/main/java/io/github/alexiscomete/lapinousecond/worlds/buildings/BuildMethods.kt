package io.github.alexiscomete.lapinousecond.worlds.buildings

import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI

interface BuildMethods {
    fun getCompleteInfos(ui: PlayerUI): LongCustomUI
    fun configBuilding()
    fun interpret(args: Array<String>)
    val help: String?
    val usage: String?
    fun descriptionShort(): String
    fun title(): String
}