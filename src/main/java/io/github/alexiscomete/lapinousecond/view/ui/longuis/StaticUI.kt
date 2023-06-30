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
        // add to the first line where there is space (5 spaces on one line)
        for (i in interactions.indices) {
            if (interactions[i].size < 5) {
                val lineI = interactions[i].plus(interactionUICustomUI)
                // replace the line
                interactions = interactions.mapIndexed { index, list ->
                    if (index == i) {
                        lineI
                    } else {
                        list
                    }
                }
                return this
            }
        }
        // if there is no space, add a new line
        interactions = interactions.plus(listOf(listOf(interactionUICustomUI)))
        return this
    }

    override fun addInteractionUICustomUIs(interactionUICustomUIs: List<InteractionUICustomUI>): LongCustomUI {
        interactions = interactions.plus(listOf(interactionUICustomUIs))
        return this
    }
}