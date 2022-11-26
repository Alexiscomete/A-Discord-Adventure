package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.entity.Player
import java.awt.image.BufferedImage

class DiscordPlayerUI(private val player: Player) : PlayerUI {
    override fun addMessage(message: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun addDialogue(dialogue: Dialogue): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun addInteraction(id: String, interaction: InteractionUI): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun respondToInteraction(id: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun respondToInteractionWithArgument(id: String, argument: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun removeInteraction(id: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun clear(): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun updateOrSend(): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun setImage(image: BufferedImage): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun setImage(link: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun hasInteraction(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasDialogue(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasMessage(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasImage(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getInteraction(id: String): InteractionUI? {
        TODO("Not yet implemented")
    }

    override fun getImage(): BufferedImage? {
        TODO("Not yet implemented")
    }

    override fun getInteractions(): Map<String, InteractionUI> {
        TODO("Not yet implemented")
    }

    override fun getMessages(): List<String> {
        TODO("Not yet implemented")
    }

    override fun getDialogues(): List<Dialogue> {
        TODO("Not yet implemented")
    }

    override fun getPlayer(): Player {
        return player
    }
}