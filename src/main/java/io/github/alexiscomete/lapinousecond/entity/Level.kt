package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.useful.ProgressionBar
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet

class Level(val entity: CacheGetSet, private val field: String, private val accumulation: Double = 2.5, val start: Double = 20.0) {

    fun xpForLevel(level: Int, xpForLastLevel: Double? = null): Double {
        if (level == 1) return start
        if (level == 0) return 0.0
        return (xpForLastLevel ?: xpForLevel(level - 1)) + accumulation * level
    }

    fun levelForXp(xp: Double): Int {
        var level = 0
        var xpForLastLevel = start
        while (xpForLevel(level, xpForLastLevel) < xp) {
            xpForLastLevel = xpForLevel(level, xpForLastLevel)
            level++
        }
        return level
    }

    fun xpForNextLevel(xp: Double): Double {
        return totalXpForNextLevel(xp) - xp
    }

    fun totalXpForNextLevel(xp: Double): Double {
        val level = levelForXp(xp)
        return xpForLevel(level + 1)
    }

    fun xpForLastLevel(xp: Double): Double {
        val level = levelForXp(xp)
        return xpForLevel(level)
    }

    fun xpInCurrentLevel(xp: Double): Double {
        return xp - xpForLastLevel(xp)
    }

    val level
        get() = levelForXp(if (entity[field] == "") 0.0 else entity[field].toDouble())

    val xpForNextLevel
        get() = xpForNextLevel(if (entity[field] == "") 0.0 else entity[field].toDouble())

    val totalXpForNextLevel
        get() = totalXpForNextLevel(if (entity[field] == "") 0.0 else entity[field].toDouble())

    val xpForLastLevel
        get() = xpForLastLevel(if (entity[field] == "") 0.0 else entity[field].toDouble())

    val xpInCurrentLevel
        get() = xpInCurrentLevel(if (entity[field] == "") 0.0 else entity[field].toDouble())

    fun addXp(xp: Double) {
        entity[field] = ((if (entity[field] == "") 0.0 else entity[field].toDouble()) + xp).toString()
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
            xpForNextLevel,
            xpInCurrentLevel,
            15
        ).bar + " **$level** ($xpInCurrentLevel/$xpForNextLevel)"
    }
}