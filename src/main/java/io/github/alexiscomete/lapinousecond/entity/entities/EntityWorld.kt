package io.github.alexiscomete.lapinousecond.entity.entities

import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum

class EntityWorld(
    val entity: CacheGetSet,
    val world: WorldEnum
) {
}