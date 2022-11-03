package io.github.alexiscomete.lapinousecond.worlds.buildings

import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.ui.MenuBuilder

interface BuildMethods {
    fun getCompleteInfos(p: Context): MenuBuilder
    fun configBuilding()
    fun interpret(args: Array<String>)
    val help: String?
    val usage: String?
    fun descriptionShort(): String
    fun title(): String
}