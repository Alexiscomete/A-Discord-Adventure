package io.github.alexiscomete.lapinousecond.entity.entities

import io.github.alexiscomete.lapinousecond.data.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.entity.effects.EffectsManager
import io.github.alexiscomete.lapinousecond.entity.xp.Level

class PlayerManager private constructor(
    id: Long
) {
    companion object {
        private val playerManagers = CacheCustom(PLAYERS) { PlayerManager(it) }
        operator fun get(id: Long): PlayerManager {
            return playerManagers[id] ?: throw NullPointerException("PlayerManager $id is null. Please create an account first with /account start.")
        }

        fun getOrNull(id: Long): PlayerManager? = playerManagers[id]

        fun createAccount(id: Long): PlayerManager {
            val pM = playerManagers[id]
            if (pM != null) {
                throw NullPointerException("PlayerManager $id already exists.")
            }
            playerManagers.add(id)
            return playerManagers[id] ?: throw NullPointerException("WARNING : your account cannot be found or created. Please contact the bot owner, or try again.")
        }
    }

    val playerData: PlayerData = PlayerData(id)

    val level: Level = Level(playerData, "xp")
    var lastLevelUpdate = 0L
    val effectsManager = EffectsManager()
    val playerRManager = PlayerRManager(playerData)
}