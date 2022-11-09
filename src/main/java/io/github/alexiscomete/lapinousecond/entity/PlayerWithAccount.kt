package io.github.alexiscomete.lapinousecond.entity

import org.javacord.api.entity.user.User

class PlayerWithAccount(val user: User) {
    val player = players[user.id] ?: throw IllegalStateException("Player not found")

    operator fun get(key: String): String {
        return player[key]
    }

    operator fun set(key: String, value: String) {
        player[key] = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerWithAccount

        return (user.id == other.user.id)
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + player.hashCode()
        return result
    }
}