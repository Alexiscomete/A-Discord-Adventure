package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.PlayerUI
import java.awt.image.BufferedImage

abstract class BaseUI(
    private var linkedImage: String?,
    private var bufferedImage: BufferedImage?,
    private var title: String?,
    private var description: String?,
    private var playerUI: PlayerUI,
    private var underString: String?
) : LongCustomUI {

    private val defaultImage = linkedImage
    private val defaultBufferedImage = bufferedImage
    private val defaultTitle = title
    private val defaultDescription = description
    private val defaultUnderString = underString

    override fun getTitle(): String? {
        return title
    }

    override fun setTitle(title: String?): LongCustomUI {
        this.title = title ?: defaultTitle
        return this
    }

    override fun getDescription(): String? {
        return description
    }

    override fun setDescription(description: String?): LongCustomUI {
        this.description = description ?: defaultDescription
        return this
    }

    override fun getLinkedImage(): String? {
        return linkedImage
    }

    override fun setLinkedImage(linkedImage: String?): LongCustomUI {
        this.linkedImage = linkedImage ?: defaultImage
        return this
    }

    override fun getBufferedImage(): BufferedImage? {
        return bufferedImage
    }

    override fun setBufferedImage(bufferedImage: BufferedImage?): LongCustomUI {
        this.bufferedImage = bufferedImage ?: defaultBufferedImage
        return this
    }

    override fun getUnderString(): String? {
        return underString
    }

    override fun setUnderString(underString: String?): LongCustomUI {
        this.underString = underString ?: defaultUnderString
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
        return playerUI
    }

    override fun setPlayerUI(everyUI: PlayerUI): LongCustomUI {
        this.playerUI = everyUI
        return this
    }
}