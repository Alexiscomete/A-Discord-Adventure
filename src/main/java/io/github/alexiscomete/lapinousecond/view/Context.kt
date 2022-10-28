package io.github.alexiscomete.lapinousecond.view

import io.github.alexiscomete.lapinousecond.entity.PlayerWithAccount
import io.github.alexiscomete.lapinousecond.view.message_event.ButtonsContextManager
import org.javacord.api.event.interaction.SelectMenuChooseEvent

data class Players(val player: PlayerWithAccount, val otherPlayers: List<PlayerWithAccount> = listOf())

val contexts = mutableMapOf<Players, Context>()

fun contextForOrNull(player: PlayerWithAccount, otherPlayers: List<PlayerWithAccount>): Context? {
    return contexts[Players(player, otherPlayers)]
}

fun contextFor(player: PlayerWithAccount, otherPlayers: List<PlayerWithAccount> = listOf()): Context {
    return contextForOrNull(player, otherPlayers)
        ?: run {
            val players = Players(player, otherPlayers)
            Context(players)
                .also {
                    contexts[players] = it
                }
        }
}

fun contextFor(players: List<PlayerWithAccount>): Context {
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

class Context(val players: Players?) {
    val buttons: ButtonsContextManager? = null
    val selectMenu: ((SelectMenuChooseEvent, Context) -> Unit)? = null
    val multiContext: Context? = null

}