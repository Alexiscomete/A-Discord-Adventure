package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.view.ui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.PlayerUI
import java.awt.image.BufferedImage

class InvInfosUI : LongCustomUI {
    override fun getTitle(): String {
        return "Informations sur le joueur"
    }

    override fun setTitle(title: String?): LongCustomUI {
        return this
    }

    override fun getDescription(): String? {
        TODO("Not yet implemented")
    }

    override fun setDescription(description: String?): LongCustomUI {
        TODO("Not yet implemented")
    }

    override fun getLinkedImage(): String {
        return "https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png"
    }

    override fun setLinkedImage(linkedImage: String?): LongCustomUI {
        return this
    }

    override fun getBufferedImage(): BufferedImage? {
        TODO("Not yet implemented")
    }

    override fun setBufferedImage(bufferedImage: BufferedImage?): LongCustomUI {
        TODO("Not yet implemented")
    }

    override fun getUnderString(): String {
        return "<t:${System.currentTimeMillis()}>"
    }

    override fun setUnderString(underString: String?): LongCustomUI {
        return this
    }

    override fun getInteractionUICustomUILists(): List<List<InteractionUICustomUI>> {
        TODO("Not yet implemented")
    }

    override fun setInteractionUICustomUIs(interactionUICustomUIs: List<List<InteractionUICustomUI>>): LongCustomUI {
        TODO("Not yet implemented")
    }

    override fun addInteractionUICustomUI(interactionUICustomUI: InteractionUICustomUI): LongCustomUI {
        TODO("Not yet implemented")
    }

    override fun addInteractionUICustomUIs(interactionUICustomUIs: List<InteractionUICustomUI>): LongCustomUI {
        TODO("Not yet implemented")
    }

    override fun hasInteractionID(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun respondToInteraction(id: String): LongCustomUI {
        TODO("Not yet implemented")
    }

    override fun respondToInteraction(id: String, argument: String): LongCustomUI {
        TODO("Not yet implemented")
    }

    override fun getPlayerUI(): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun setPlayerUI(everyUI: PlayerUI): LongCustomUI {
        TODO("Not yet implemented")
    }
}