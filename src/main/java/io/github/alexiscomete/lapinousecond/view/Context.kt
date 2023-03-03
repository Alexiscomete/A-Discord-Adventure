package io.github.alexiscomete.lapinousecond.view

import io.github.alexiscomete.lapinousecond.entity.PlayerWithAccount
import io.github.alexiscomete.lapinousecond.view.contextmanager.ButtonsContextManager
import io.github.alexiscomete.lapinousecond.view.contextmanager.ContextManager
import io.github.alexiscomete.lapinousecond.view.contextmanager.ModalContextManager
import io.github.alexiscomete.lapinousecond.view.contextmanager.SelectMenuContextManager
import io.github.alexiscomete.lapinousecond.view.ui.playerui.DiscordPlayerUI
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.ModalSubmitEvent
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

class Context(val players: Players, canParallel: Boolean = false) : ContextManager {

    init {
        if (players.otherPlayers.isNotEmpty()) {
            for (player in players.otherPlayers) {
                contextFor(player).also {
                    it.multiContext(this, canParallel)
                }
            }
        }
    }

    private var buttons: ButtonsContextManager? = null
    private var selectMenu: SelectMenuContextManager? = null
    private var multiContext: Context? = null
    private var modal: ModalContextManager? = null
    private var ui: DiscordPlayerUI? = null

    fun buttons(buttons: ButtonsContextManager, canParallel: Boolean = false): Context {
        if (!canParallel) {
            clear()
        }
        this.buttons = buttons
        return this
    }

    fun selectMenu(selectMenu: SelectMenuContextManager, canParallel: Boolean = false): Context {
        if (!canParallel) {
            clear()
        }
        this.selectMenu = selectMenu
        return this
    }

    fun multiContext(multiContext: Context, canParallel: Boolean = false): Context {
        if (!canParallel) {
            clear()
        }
        this.multiContext = multiContext
        return this
    }

    fun modal(modal: ModalContextManager, canParallel: Boolean = false): Context {
        if (!canParallel) {
            clear()
        }
        this.modal = modal
        return this
    }

    fun ui(ui: DiscordPlayerUI, canParallel: Boolean = false): Context {
        if (!canParallel) {
            clear()
        }
        this.ui = ui
        return this
    }

    private fun clear() {
        buttons = null
        selectMenu = null
        multiContext = null
        modal = null
        ui = null
    }

    override fun canApply(string: String): Boolean {
        if (multiContext != null && multiContext!!.canApply(string)) {
            return true
        }
        if (buttons != null && buttons!!.canApply(string)) {
            return true
        }
        if (selectMenu != null && selectMenu!!.canApply(string)) {
            return true
        }
        if (modal != null && modal!!.canApply(string)) {
            return true
        }
        if (ui != null && ui!!.canExecute(string)) {
            return true
        }
        return false
    }

    fun apply(string: String, event: ButtonClickEvent) {
        if (multiContext != null && multiContext!!.canApply(string)) {
            multiContext!!.apply(string, event)
            return
        }
        if (ui != null && ui!!.canExecute(string)) {
            ui!!.interaction = event.interaction
            ui!!.respondToInteraction(string)
            return
        }
        if (buttons != null && buttons!!.canApply(string)) {
            val button = buttons!!.hash[string]
            if (button != null) {
                button(event, this, buttons!!)
                return
            }
            return
        }
        throw IllegalStateException("Cannot apply $string. ${if (buttons != null) buttons.toString() else "No buttons"}, ${if (multiContext != null) multiContext.toString() else "No multiContext"} => Cette interaction n'est plus valide, recommencez : le bot supprime les anciennes interactions de sa mémoire afin de ne pas se mélanger les pinceaux")
    }

    fun apply(string: String, event: SelectMenuChooseEvent) {
        if (multiContext != null && multiContext!!.canApply(string)) {
            multiContext!!.apply(string, event)
            return
        }
        if (ui != null && ui!!.canExecute(string)) {
            ui!!.interaction = event.interaction
            ui!!.respondToInteraction(string)
            return
        }
        if (selectMenu != null) {
            if (selectMenu!!.canApply(string)) {
                selectMenu!!.ex(event, this)
                return
            }
        }
        throw IllegalStateException("Cannot apply $string. ${if (selectMenu != null) selectMenu.toString() else "No selectMenu"}, ${if (multiContext != null) multiContext.toString() else "No multiContext"} => Cette interaction n'est plus valide, recommencez : le bot supprime les anciennes interactions de sa mémoire afin de ne pas se mélanger les pinceaux")
    }

    fun apply(customId: String, p0: ModalSubmitEvent) {
        if (multiContext != null) {
            if (multiContext!!.canApply(customId)) {
                multiContext!!.apply(customId, p0)
                return
            }
        }
        if (modal != null) {
            if (modal!!.canApply(customId)) {
                modal!!.ex(p0, this)
                return
            }
        }
        throw IllegalStateException("Cannot apply $customId. ${if (modal != null) modal.toString() else "No modal"}, ${if (multiContext != null) multiContext.toString() else "No multiContext"} => Cette interaction n'est plus valide, recommencez : le bot supprime les anciennes interactions de sa mémoire afin de ne pas se mélanger les pinceaux")
    }

    override fun toString(): String {
        return "Context(players=$players, buttons=$buttons, selectMenu=$selectMenu, multiContext=$multiContext, modal=$modal)"
    }
}