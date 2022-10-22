package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet

class Level(entity: CacheGetSet, field: String, val accumulation: Double = 2.5, val start: Double = 20.0) {

    fun xpForLevel(level: Int, xpForLastLevel: Double? = null): Double {
        if (level == 1) return start
        if (level == 0) return 0.0
        return (xpForLastLevel ?: xpForLevel(level - 1)) + accumulation * level
    }

    fun levelForXp(xp: Double): Int {
        var level = 1
        var xpForLastLevel = start
        while (xpForLevel(level, xpForLastLevel) < xp) {
            xpForLastLevel = xpForLevel(level, xpForLastLevel)
            level++
        }
        return level
    }

    fun xpForNextLevel(xp: Double): Double {
        val level = levelForXp(xp)
        return xpForLevel(level + 1) - xp
    }

    fun xpForLastLevel(xp: Double): Double {
        val level = levelForXp(xp)
        return xpForLevel(level)
    }

    fun xpInCurrentLevel(xp: Double): Double {
        val level = levelForXp(xp)
        return xp - xpForLevel(level)
    }
}