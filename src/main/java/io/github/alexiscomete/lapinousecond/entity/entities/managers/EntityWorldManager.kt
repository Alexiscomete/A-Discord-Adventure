package io.github.alexiscomete.lapinousecond.entity.entities.managers

import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum

class EntityWorldManager(
    val data: CacheGetSet
) {
    private var worldNameCache = data["world"]

    val world: WorldEnum
        get() {
            val w = data["world"]
            if (w == "") {
                data["world"] = WorldEnum.TUTO.progName
                return WorldEnum.TUTO
            }
            return WorldEnum.valueOf(w)
        }

    // EntityWorld is a class that represents the world of an entity. It is cached and a new one is created when worldName changes.

    private var entityWorldCache = EntityWorld(data, world)
    val entityWorld: EntityWorld
        get() {
            val worldName = data["world"]
            if (worldName != worldNameCache) {
                worldNameCache = worldName
                entityWorldCache = EntityWorld(data, world)
            }
            return entityWorldCache
        }


}