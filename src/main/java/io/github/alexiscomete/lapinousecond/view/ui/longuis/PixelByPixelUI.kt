package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.*
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import java.awt.image.BufferedImage

class PixelByPixelUI(
    private var playerUI: PlayerUI,
    private var image: BufferedImage?,
    private var linkedImage: String?,
) : LongCustomUI {

    private var title: String = "Pixel by pixel"
    private var description = "Move with buttons"
    private var currentZoomCoefficient: Int = 1

    private val player
        get() = playerUI.getPlayer()
    private val worldStr
        get() = player["world"]

    private val world
        get() = WorldEnum.valueOf(worldStr)

    private var x: Int
        get() = player["place_${worldStr}_x"].toInt()
        set(value) {
            player["place_${worldStr}_x"] = value.toString()
        }

    private var y: Int
        get() = player["place_${worldStr}_y"].toInt()
        set(value) {
            player["place_${worldStr}_y"] = value.toString()
        }

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

    override fun getBufferedImage(): BufferedImage {
        val zoom = player["place_${worldStr}_zoom"]
        val zooms = try {
            Zooms.valueOf(zoom)
        } catch (e: Exception) {
            Zooms.ZOOM_OUT
        }
        return world.zoomWithDecorElements(x, y, 30, zooms, player)
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
                    y -= currentZoomCoefficient
                },
                { _, _ ->
                    y -= currentZoomCoefficient
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
                    x -= currentZoomCoefficient
                },
                { _, _ ->
                    x -= currentZoomCoefficient
                },
            ),
            SimpleInteractionUICustomUI(
                "down",
                "⬇",
                "Move down",
                this,
                InteractionStyle.NORMAL,
                {
                    y += currentZoomCoefficient
                },
                { _, _ ->
                    y += currentZoomCoefficient
                },
            ),
            SimpleInteractionUICustomUI(
                "right",
                "➡",
                "Move right",
                this,
                InteractionStyle.NORMAL,
                {
                    x += currentZoomCoefficient
                },
                { _, _ ->
                    x += currentZoomCoefficient
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