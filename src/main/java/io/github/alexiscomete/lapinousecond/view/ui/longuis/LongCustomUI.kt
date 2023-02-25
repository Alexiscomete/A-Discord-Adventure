package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionUICustomUI
import java.awt.image.BufferedImage

interface LongCustomUI {

    // title
    fun getTitle(): String?
    fun setTitle(title: String?): LongCustomUI

    // description
    fun getDescription(): String?
    fun setDescription(description: String?): LongCustomUI

    // fields
    fun getFields(): List<Pair<String, String>>?

    // image
    // TODO : change to pixels to be more flexible ?
    fun getLinkedImage(): String?
    fun setLinkedImage(linkedImage: String?): LongCustomUI
    fun getBufferedImage(): BufferedImage?
    fun setBufferedImage(bufferedImage: BufferedImage?): LongCustomUI

    // under string (like a footer)
    fun getUnderString(): String?
    fun setUnderString(underString: String?): LongCustomUI

    // InteractionUICustomUI
    fun getInteractionUICustomUILists(): List<List<InteractionUICustomUI>>
    fun setInteractionUICustomUIs(interactionUICustomUIs: List<List<InteractionUICustomUI>>): LongCustomUI
    fun addInteractionUICustomUI(interactionUICustomUI: InteractionUICustomUI): LongCustomUI
    fun addInteractionUICustomUIs(interactionUICustomUIs: List<InteractionUICustomUI>): LongCustomUI
    fun hasInteractionID(id: String): Boolean
    fun respondToInteraction(id: String): LongCustomUI
    fun respondToInteraction(id: String, argument: String): LongCustomUI

    // every UI
    fun getPlayerUI(): PlayerUI
    fun setPlayerUI(everyUI: PlayerUI): LongCustomUI

}