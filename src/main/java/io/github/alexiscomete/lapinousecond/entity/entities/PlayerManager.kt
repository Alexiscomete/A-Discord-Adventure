package io.github.alexiscomete.lapinousecond.entity.entities

import io.github.alexiscomete.lapinousecond.data.managesave.CacheCustom

class PlayerManager private constructor(
    id: Long
) {
    companion object {
        private val playerManagers = CacheCustom(PLAYERS) { PlayerManager(it) }
        operator fun get(id: Long): PlayerManager {
            val playerManager = playerManagers[id]
            if (playerManager == null) {
                throw NullPointerException("PlayerManager $id is null")
            }
            return playerManager
        }

        fun getOrNull(id: Long): PlayerManager? = playerManagers[id]

        fun createAccount(id: Long): PlayerManager {
            val pM = playerManagers[id]
            if (pM != null) {
                throw NullPointerException("PlayerManager $id already exists")
            }
            playerManagers.add(id)
            return playerManagers[id]!!
        }
    }

    val playerData: PlayerData = PlayerData(id)
}