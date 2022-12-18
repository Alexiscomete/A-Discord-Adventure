package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.*
import java.awt.image.BufferedImage

class PixelByPixelUI(
    private var playerUI: PlayerUI,
    private var image: BufferedImage?,
    private var linkedImage: String?,
    private var x: Int,
    private var y: Int,
) : LongCustomUI {

    private var title: String = "Pixel by pixel"
    private var description = "Move with buttons"

    override fun getTitle(): String {
        return title
    }

    override fun setTitle(title: String?): LongCustomUI {
        this.title = title ?: "Pixel by pixel"
        return this
    }

    override fun getDescription(): String {
        return description
    }

    override fun setDescription(description: String?): LongCustomUI {
        this.description = description ?: "Move with buttons"
        return this
    }

    override fun getLinkedImage(): String? {
        return linkedImage
    }

    override fun setLinkedImage(linkedImage: String?): LongCustomUI {
        this.linkedImage = linkedImage
        return this
    }

    override fun getBufferedImage(): BufferedImage? {
        return image
    }

    override fun setBufferedImage(bufferedImage: BufferedImage?): LongCustomUI {
        this.image = bufferedImage
        return this
    }

    override fun getUnderString(): String {
        return "You are at ($x, $y)"
    }

    override fun setUnderString(underString: String?): LongCustomUI {
        return this
    }

    private var interactionUICustomUILists: List<List<InteractionUICustomUI>> = listOf(
        listOf(
            DisabledUI(
                this,
                InteractionStyle.NORMAL_DISABLED,
                "none0",
                " "
            ),
            SimpleInteractionUICustomUI(
                "up",
                "⬆",
                "Move up",
                this,
                InteractionStyle.NORMAL,
                {
                    y--
                },
                { _, _ ->
                    y--
                },
            ),
            DisabledUI(
                this,
                InteractionStyle.NORMAL_DISABLED,
                "none2",
                " "
            ),
        ),
        listOf(
            SimpleInteractionUICustomUI(
                "left",
                "⬅",
                "Move left",
                this,
                InteractionStyle.NORMAL,
                {
                    x--
                },
                { _, _ ->
                    x--
                },
            ),
            SimpleInteractionUICustomUI(
                "down",
                "⬇",
                "Move down",
                this,
                InteractionStyle.NORMAL,
                {
                    y++
                },
                { _, _ ->
                    y++
                },
            ),
            SimpleInteractionUICustomUI(
                "right",
                "➡",
                "Move right",
                this,
                InteractionStyle.NORMAL,
                {
                    x++
                },
                { _, _ ->
                    x++
                },
            )
        ),
    )

    override fun getInteractionUICustomUILists(): List<List<InteractionUICustomUI>> {
        return interactionUICustomUILists
    }

    override fun setInteractionUICustomUIs(interactionUICustomUIs: List<List<InteractionUICustomUI>>): LongCustomUI {
        this.interactionUICustomUILists = interactionUICustomUIs
        return this
    }

    override fun addInteractionUICustomUI(interactionUICustomUI: InteractionUICustomUI): LongCustomUI {
        interactionUICustomUILists = interactionUICustomUILists.plus(listOf(listOf(interactionUICustomUI)))
        return this
    }

    override fun addInteractionUICustomUIs(interactionUICustomUIs: List<InteractionUICustomUI>): LongCustomUI {
        interactionUICustomUILists = interactionUICustomUILists.plus(listOf(interactionUICustomUIs))
        return this
    }

    override fun hasInteractionID(id: String): Boolean {
        return interactionUICustomUILists.flatten().any { it.getId() == id }
    }

    override fun respondToInteraction(id: String): LongCustomUI {
        interactionUICustomUILists.flatten().first { it.getId() == id }.execute(playerUI)
        return this
    }

    override fun respondToInteraction(id: String, argument: String): LongCustomUI {
        interactionUICustomUILists.flatten().first { it.getId() == id }.executeWithArgument(playerUI, argument)
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