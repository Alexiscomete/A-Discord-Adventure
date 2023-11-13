package io.github.alexiscomete.lapinousecond.entity.xp

import io.github.alexiscomete.lapinousecond.data.dataclass.ProgressionBar
import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import kotlin.math.roundToInt

const val ROUND_PRECISION_D = 1000.0
const val PROGRESSION_STRING_SIZE = 15

class Level(
    val entity: CacheGetSet,
    private val field: String,
    accumulation: Double = 2.5,
    start: Double = 2.5
) : ComputeLevel(accumulation, start) {

    val level
        get() = levelForXp(if (entity[field] == "") 0.0 else entity[field].toDouble())

    val xpForNextLevel
        get() = xpForNextLevel(if (entity[field] == "") 0.0 else entity[field].toDouble())

    val totalXpForNextLevel
        get() = totalXpForNextLevel(if (entity[field] == "") 0.0 else entity[field].toDouble())

    val xpForLastLevel
        get() = totalXpForCurrentLevel(if (entity[field] == "") 0.0 else entity[field].toDouble())

    val xpInCurrentLevel
        get() = xpInCurrentLevel(if (entity[field] == "") 0.0 else entity[field].toDouble())

    fun addXp(xp: Double): Pair<Int, Int>? {
        val currentLevel = level
        entity[field] = ((if (entity[field] == "") 0.0 else entity[field].toDouble()) + xp).toString()
        val newLevel = level
        if (currentLevel != newLevel) {
            return Pair(currentLevel, newLevel)
        }
        return null
    }

    fun removeXp(xp: Double) {
        entity[field] = ((if (entity[field] == "") 0.0 else entity[field].toDouble()) - xp).toString()
        if (entity[field].toDouble() < 0) entity[field] = "0"
    }

    fun setXp(xp: Double) {
        entity[field] = xp.toString()
    }

    fun setLevel(level: Int) {
        entity[field] = xpForLevel(level).toString()
    }

    fun addLevel(level: Int) {
        entity[field] = (xpForLevel(level) + (if (entity[field] == "") 0.0 else entity[field].toDouble())).toString()
    }

    override fun toString(): String {
        return ProgressionBar(
            "🟡",
            "🟨",
            "⬜",
            totalXpForNextLevel,
            xpInCurrentLevel,
            PROGRESSION_STRING_SIZE
        ).bar + " **$level** (${(xpInCurrentLevel * ROUND_PRECISION_D).roundToInt() / ROUND_PRECISION_D}/${(totalXpForNextLevel * ROUND_PRECISION_D).roundToInt() / ROUND_PRECISION_D})"
    }
}
