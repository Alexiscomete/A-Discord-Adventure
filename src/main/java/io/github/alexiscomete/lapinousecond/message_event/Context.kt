package io.github.alexiscomete.lapinousecond.message_event

import io.github.alexiscomete.lapinousecond.entity.Player

data class Players(val player: Player, val otherPlayers: List<Player> = listOf())

val contexts = mutableMapOf<Players, Context>()

fun contextForOrNull(player: Player, otherPlayers: List<Player>): Context? {
    return contexts[Players(player, otherPlayers)]
}

fun contextFor(player: Player, otherPlayers: List<Player>): Context {
    return contextForOrNull(player, otherPlayers)
        ?: run {
            val players = Players(player, otherPlayers)
            Context(players)
                .also {
                    contexts[players] = it
                }
        }
}

fun contextFor(players: List<Player>): Context {
    // for each combination of players
    for (player in players) {
        players.minus(player).also { list ->
            if (list.isNotEmpty()) {
                contextForOrNull(player, list)?.let { return it }
            }
        }
    }
    return contextFor(players.first(), players.minus(players.first()))
}

class Context(val players: Players) {

}