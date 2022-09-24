package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet

class Level(entity: CacheGetSet, field: String, val accumulation: Double = 2.5, val start: Double = 20.0) {

    fun xpForLevel(level: Int): Double {
        if (level == 1) return start
        if (level == 0) return 0.0
        return xpForLevel(level - 1) + accumulation * level
    }


}