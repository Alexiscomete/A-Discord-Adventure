package io.github.alexiscomete.lapinousecond.worlds

open class World(
    val typeOfServer: String,
    val name: String,
    val progName: String,
    val desc: String,
    val defaultX: Int,
    val defaultY: Int,
    val mapPath: String,
    val mapWidth: Int,
    val mapHeight: Int
)