package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.entity.Player
import java.awt.image.BufferedImage

interface PlayerUI {
    fun addMessage(message: Message): PlayerUI
    fun addDialogue(dialogue: Dialogue): PlayerUI
    fun addInteraction(id: String, interaction: InteractionUI): PlayerUI
    fun respondToInteraction(id: String): PlayerUI
    fun respondToInteractionWithArgument(id: String, argument: String): PlayerUI
    fun removeInteraction(id: String): PlayerUI
    fun clear(): PlayerUI
    fun updateOrSend(): PlayerUI
    fun setImage(image: BufferedImage): PlayerUI
    fun setImage(link: String): PlayerUI
    fun hasInteraction(id: String): Boolean
    fun hasDialogue(): Boolean
    fun hasMessage(): Boolean
    fun hasBufferedImage(): Boolean
    fun hasLinkedImage(): Boolean
    fun getInteraction(id: String): InteractionUI?
    fun getBufferedImage(): BufferedImage
    fun getLinkedImage(): String
    fun getInteractions(): Map<String, InteractionUI>
    fun getMessages(): List<String>
    fun getDialogues(): List<Dialogue>
    fun getPlayer(): Player
}