package io.github.alexiscomete.lapinousecond.view.ui.playerui

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerData
import io.github.alexiscomete.lapinousecond.view.ui.dialogue.Dialogue
import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI

interface PlayerUI {
    fun addMessage(message: Message): PlayerUI
    fun addDialogue(dialogue: Dialogue): PlayerUI
    fun respondToInteraction(id: String): PlayerUI
    fun respondToInteraction(id: String, argument: String): PlayerUI
    fun clear(): PlayerUI
    fun updateOrSend(): PlayerUI
    fun hasDialogue(): Boolean
    fun hasMessage(): Boolean
    fun getMessages(): List<String>
    fun getDialogues(): List<Dialogue>
    fun getPlayer(): PlayerData
    fun getLongCustomUI(): LongCustomUI?
    fun setLongCustomUI(longCustomUI: LongCustomUI?): PlayerUI
    fun canExecute(id: String): Boolean
}