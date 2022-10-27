package io.github.alexiscomete.lapinousecond.entity

import org.javacord.api.entity.user.User

class PlayerWithAccount(id: Long, val user: User) : Player(id)