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
}