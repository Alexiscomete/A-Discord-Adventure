package io.github.alexiscomete.lapinousecond.entity.entities

import io.github.alexiscomete.lapinousecond.data.dataclass.ProgressionBar
import io.github.alexiscomete.lapinousecond.data.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.entity.effects.EffectsManager
import io.github.alexiscomete.lapinousecond.entity.entities.managers.EntityWorldManager
import io.github.alexiscomete.lapinousecond.entity.entities.managers.PlayerOwnerManager
import io.github.alexiscomete.lapinousecond.entity.entities.managers.WorkManager
import io.github.alexiscomete.lapinousecond.entity.xp.Level
import io.github.alexiscomete.lapinousecond.view.discord.commands.classes.ROUND_PRECISION

class PlayerManager private constructor(
    val id: Long
) {
    companion object {
        private val playerManagers = CacheCustom(PLAYERS) { PlayerManager(it) }
        operator fun get(id: Long): PlayerManager {
            return playerManagers[id]
                ?: throw NullPointerException("PlayerManager $id is null. Please create an account first with /account start.")
        }

        fun getOrNull(id: Long): PlayerManager? = playerManagers[id]

        fun createAccount(id: Long): PlayerManager {
            val pM = playerManagers[id]
            if (pM != null) {
                throw NullPointerException("PlayerManager $id already exists.")
            }
            playerManagers.add(id)
            return playerManagers[id]
                ?: throw NullPointerException("WARNING : your account cannot be found or created. Please contact the bot owner, or try again.")
        }
    }

    val playerData: PlayerData = PlayerData(id)

    val level: Level = Level(playerData, "xp")
    var lastLevelUpdate = 0L
    val effectsManager = EffectsManager()
    val ownerManager = PlayerOwnerManager(playerData)
    val worldManager = EntityWorldManager(playerData)
    val workManager = WorkManager()

    // temporary
    fun getLifeString(): String {
        var life = try {
            playerData["life"].toDouble()
        } catch (e: Exception) {
            90.0
        }
        var maxLife = try {
            playerData["maxLife"].toDouble()
        } catch (e: Exception) {
            100.0
        }
        // round to 3 decimals, à vérifier
        life = (life * ROUND_PRECISION) / ROUND_PRECISION
        maxLife = (maxLife * ROUND_PRECISION) / ROUND_PRECISION
        playerData["life"] = life.toString()
        playerData["maxLife"] = maxLife.toString()
        val progressionBar = ProgressionBar(
            "❤️", "🟥", "💔",
            maxLife, life, 10
        )
        return "$progressionBar $life/$maxLife"
    }
}