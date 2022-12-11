package io.github.alexiscomete.lapinousecond.view.ui

import java.awt.image.BufferedImage

interface LongCustomUI {

    // title
    fun getTitle(): String?
    fun setTitle(title: String?): LongCustomUI

    // description
    fun getDescription(): String?
    fun setDescription(description: String?): LongCustomUI

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
    fun getInteractionUICustomUIs(): List<InteractionUICustomUI>
    fun setInteractionUICustomUIs(interactionUICustomUIs: List<InteractionUICustomUI>): LongCustomUI
    fun addInteractionUICustomUI(interactionUICustomUI: InteractionUICustomUI): LongCustomUI

    // every UI
    fun getEveryUI(): EveryUI
    fun setEveryUI(everyUI: EveryUI): LongCustomUI

}