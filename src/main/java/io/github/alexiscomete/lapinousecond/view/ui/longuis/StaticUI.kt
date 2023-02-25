package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import java.awt.image.BufferedImage

abstract class StaticUI(
    interactionUICustomUILists: List<List<InteractionUICustomUI>>,
    playerUI: PlayerUI
) : BaseRespondUI(
    interactionUICustomUILists,
    playerUI
) {
    override fun setBufferedImage(bufferedImage: BufferedImage?): LongCustomUI {
        return this
    }

    override fun setLinkedImage(linkedImage: String?): LongCustomUI {
        return this
    }

    override fun setDescription(description: String?): LongCustomUI {
        return this
    }

    override fun setUnderString(underString: String?): LongCustomUI {
        return this
    }

    override fun setTitle(title: String?): LongCustomUI {
        return this
    }

    override fun setInteractionUICustomUIs(interactionUICustomUIs: List<List<InteractionUICustomUI>>): LongCustomUI {
        return this
    }

    override fun setPlayerUI(everyUI: PlayerUI): LongCustomUI {
        return this
    }

    override fun addInteractionUICustomUI(interactionUICustomUI: InteractionUICustomUI): LongCustomUI {
        return this
    }

    override fun addInteractionUICustomUIs(interactionUICustomUIs: List<InteractionUICustomUI>): LongCustomUI {
        return this
    }
}