package io.github.alexiscomete.lapinousecond.entity.xp

import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.entity.entities.managers.PlayerOwnerManager

class PlayerLevel(
    entity: CacheGetSet,
    val ownerManager: PlayerOwnerManager,
    field: String
) : Level(entity, field) {
    fun addXpWithReward(xp: Double): LevelRewards? {
        val pair = super.addXp(xp)
        if (pair != null) {
            val rewards = LevelRewards(pair.second)
            applyLevelRewardsTo(ownerManager, rewards)
            return rewards
        }
        return null
    }
}
