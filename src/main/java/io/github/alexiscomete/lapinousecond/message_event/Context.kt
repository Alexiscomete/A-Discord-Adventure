package io.github.alexiscomete.lapinousecond.message_event

import io.github.alexiscomete.lapinousecond.entity.Player

class Context(val player: Player, val otherPlayers: List<Player> = listOf()) {

}