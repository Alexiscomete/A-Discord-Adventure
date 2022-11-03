package io.github.alexiscomete.lapinousecond.view

import io.github.alexiscomete.lapinousecond.entity.PlayerWithAccount
import io.github.alexiscomete.lapinousecond.view.contextmanager.*
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.event.message.MessageCreateEvent

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
    private var messages: MessagesContextManager? = null

    fun buttons(buttons: ButtonsContextManager, canParallel:Boolean=false): Context {
        if (!canParallel) {
            clear()
        }
        this.buttons = buttons
        return this
    }

    fun selectMenu(selectMenu: SelectMenuContextManager, canParallel:Boolean=false): Context {
        if (!canParallel) {
            clear()
        }
        this.selectMenu = selectMenu
        return this
    }

    fun multiContext(multiContext: Context, canParallel:Boolean=false): Context {
        if (!canParallel) {
            clear()
        }
        this.multiContext = multiContext
        return this
    }

    fun modal(modal: ModalContextManager, canParallel:Boolean=false): Context {
        if (!canParallel) {
            clear()
        }
        this.modal = modal
        return this
    }

    fun messages(messages: MessagesContextManager, canParallel:Boolean=false): Context {
        if (!canParallel) {
            clear()
        }
        this.messages = messages
        return this
    }

    fun clear() {
        buttons = null
        selectMenu = null
        multiContext = null
        modal = null
        messages = null
    }

    override fun canApply(string: String): Boolean {
        if (multiContext != null) {
            if (multiContext!!.canApply(string)) {
                return true
            }
        }
        if (buttons != null) {
            if (buttons!!.canApply(string)) {
                return true
            }
        }
        if (selectMenu != null) {
            if (selectMenu!!.canApply(string)) {
                return true
            }
        }
        if (modal != null) {
            if (modal!!.canApply(string)) {
                return true
            }
        }
        if (messages != null) {
            if (messages!!.canApply(string)) {
                return true
            }
        }
        return false
    }

    fun apply(string: String, event: SelectMenuChooseEvent) {
        if (multiContext != null) {
            if (multiContext!!.canApply(string)) {
                multiContext!!.apply(string, event)
                return
            }
        }
        if (selectMenu != null) {
            if (selectMenu!!.canApply(string)) {
                selectMenu!!.ex(event, this)
                return
            }
        }
        throw IllegalStateException("Cannot apply $string")
    }

    fun apply(string: String, event: ButtonClickEvent) {
        if (multiContext != null) {
            if (multiContext!!.canApply(string)) {
                multiContext!!.apply(string, event)
                return
            }
        }
        if (buttons != null) {
            if (buttons!!.canApply(string)) {
                val button = buttons!!.hash[string]
                if (button != null) {
                    button(event, this, buttons!!)
                    return
                }
                return
            }
        }
        throw IllegalStateException("Cannot apply $string")
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
        throw IllegalStateException("Cannot apply $customId")
    }

    fun apply(string: String, event: MessageCreateEvent) {
        if (multiContext != null) {
            if (multiContext!!.canApply(string)) {
                multiContext!!.apply(string, event)
                return
            }
        }
        if (messages != null) {
            if (messages!!.canApply(string)) {
                messages!!.ex(event, this)
                return
            }
        }
        throw IllegalStateException("Cannot apply $string")
    }
}