package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import java.awt.image.BufferedImage

class TestingUI(
    private var playerUI: PlayerUI
) : LongCustomUI {

    private var count: Int = 0

    override fun getTitle(): String {
        return "Testing in progress"
    }

    override fun setTitle(title: String?): LongCustomUI {
        return this
    }

    override fun getDescription(): String {
        return "Count: $count"
    }

    override fun setDescription(description: String?): LongCustomUI {
        return this
    }

    override fun getFields(): List<Pair<String, String>>? {
        return null
    }

    override fun getLinkedImage(): String? {
        return null
    }

    override fun setLinkedImage(linkedImage: String?): LongCustomUI {
        return this
    }

    override fun getBufferedImage(): BufferedImage? {
        return null
    }

    override fun setBufferedImage(bufferedImage: BufferedImage?): LongCustomUI {
        return this
    }

    override fun getUnderString(): String {
        return "Under string I am"
    }

    override fun setUnderString(underString: String?): LongCustomUI {
        return this
    }

    override fun getInteractionUICustomUILists(): List<List<InteractionUICustomUI>> {
        return listOf(listOf(
            SimpleInteractionUICustomUI(
                "count",
                "Count",
                "Test",
                this,
                InteractionStyle.NORMAL,
                null,
                null
            )
        ))
    }

    override fun setInteractionUICustomUIs(interactionUICustomUIs: List<List<InteractionUICustomUI>>): LongCustomUI {
        return this
    }

    override fun addInteractionUICustomUI(interactionUICustomUI: InteractionUICustomUI): LongCustomUI {
        return this
    }

    override fun addInteractionUICustomUIs(interactionUICustomUIs: List<InteractionUICustomUI>): LongCustomUI {
        return this
    }

    override fun hasInteractionID(id: String): Boolean {
        return id.contains("test") || id.contains("count")
    }

    override fun respondToInteraction(id: String): LongCustomUI {
        count++
        return this
    }

    override fun respondToInteraction(id: String, argument: String): LongCustomUI {
        count++
        return this
    }

    override fun getPlayerUI(): PlayerUI {
        return playerUI
    }

    override fun setPlayerUI(everyUI: PlayerUI): LongCustomUI {
        playerUI = everyUI
        return this
    }
}