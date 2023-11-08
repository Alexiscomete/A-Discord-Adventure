package io.github.alexiscomete.lapinousecond.entity.xp

import kotlin.math.sqrt

open class ComputeLevel(
    private val accumulation: Double = 2.5,
    val start: Double = 2.5
) {
    fun xpForLevel(level: Int, xpForLastLevel: Double? = null): Double {
        if (level == 1) return start
        if (level == 0) return 0.0

        if (xpForLastLevel != null) return xpForLastLevel + (accumulation * level)

        return ((level * (level + 1)) / 2.0 - 1.0) * accumulation + start;
    }

    fun levelForXp(xp: Double): Int {
        if (xp < start) return 0
        if (xp < start + accumulation * 2) return 1

        val xpCalc = ((xp - start) / accumulation + 1.0) * 2.0
        val delta = 1 + 4 * xpCalc
        return ((-1 + sqrt(delta)) / 2).toInt()
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
}
