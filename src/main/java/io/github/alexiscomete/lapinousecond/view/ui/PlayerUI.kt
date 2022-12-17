package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.ui.dialogue.Dialogue
import java.awt.image.BufferedImage

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
    fun getPlayer(): Player
    fun getLongCustomUI(): LongCustomUI?
    fun setLongCustomUI(longCustomUI: LongCustomUI?): PlayerUI
}