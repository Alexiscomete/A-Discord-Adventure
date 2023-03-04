package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Message
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import java.awt.image.BufferedImage

class PixelByPixelUI(
    private var playerUI: PlayerUI,
    private var linkedImage: String?,
) : LongCustomUI {

    private var title: String = "Pixel by pixel"
    private var description = "Move with buttons"

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

    private var zooms: Zooms
        get() = try {
            Zooms.valueOf(player["place_${worldStr}_zoom"])
        } catch (e: Exception) {
            Zooms.ZOOM_OUT
        }
        set(value) {
            val (nextX, nextY) = try {
                zooms.zoomInTo(value, x, y)
            } catch (e: Exception) {
                zooms.zoomOutTo(value, x, y)
            }
            x = nextX
            y = nextY
            player["place_${worldStr}_zoom"] = value.name
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

    override fun getFields(): List<Pair<String, String>>? {
        return null
    }

    override fun getLinkedImage(): String? {
        return linkedImage
    }

    override fun setLinkedImage(linkedImage: String?): LongCustomUI {
        this.linkedImage = linkedImage
        return this
    }

    override fun getBufferedImage(): BufferedImage {
        return world.zoomWithDecorElements(x, y, 30, zooms, player)
    }

    override fun setBufferedImage(bufferedImage: BufferedImage?): LongCustomUI {
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
            SimpleInteractionUICustomUI(
                "zoom_in",
                "🔍",
                "Zoom in",
                InteractionStyle.SECONDARY,
                {
                    zooms = zooms.next ?: run {
                        playerUI.addMessage(Message("Le monde microscopique n'est pas encore implémenté", "Problème"))
                        zooms
                    }
                    return@SimpleInteractionUICustomUI null
                }
            ) { _, _ ->
                zooms = zooms.next ?: run {
                    playerUI.addMessage(Message("Le monde microscopique n'est pas encore implémenté", "Problème"))
                    zooms
                }
                return@SimpleInteractionUICustomUI null
            },
            SimpleInteractionUICustomUI(
                "up",
                "⬆",
                "Move up",
                InteractionStyle.NORMAL,
                {
                    y--
                    return@SimpleInteractionUICustomUI null
                },
            ) { _, _ ->
                y--
                return@SimpleInteractionUICustomUI null
            },
            SimpleInteractionUICustomUI(
                "zoom_out",
                "🚁",
                "Zoom out",
                InteractionStyle.SECONDARY,
                {
                    zooms = zooms.before ?: run {
                        playerUI.addMessage(Message("Le monde des géants n'est pas encore implémenté", "Problème"))
                        zooms
                    }
                    return@SimpleInteractionUICustomUI null
                }
            ) { _, _ ->
                zooms = zooms.before ?: run {
                    playerUI.addMessage(Message("Le monde des géants n'est pas encore implémenté", "Problème"))
                    zooms
                }
                return@SimpleInteractionUICustomUI null
            },
        ),
        listOf(
            SimpleInteractionUICustomUI(
                "left",
                "⬅",
                "Move left",
                InteractionStyle.NORMAL,
                {
                    x--
                    return@SimpleInteractionUICustomUI null
                },
            ) { _, _ ->
                x--
                return@SimpleInteractionUICustomUI null
            },
            SimpleInteractionUICustomUI(
                "down",
                "⬇",
                "Move down",
                InteractionStyle.NORMAL,
                {
                    y++
                    return@SimpleInteractionUICustomUI null
                },
            ) { _, _ ->
                y++
                return@SimpleInteractionUICustomUI null
            },
            SimpleInteractionUICustomUI(
                "right",
                "➡",
                "Move right",
                InteractionStyle.NORMAL,
                {
                    x++
                    return@SimpleInteractionUICustomUI null
                },
            ) { _, _ ->
                x++
                return@SimpleInteractionUICustomUI null
            }
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

    override fun respondToInteraction(id: String): Question? {
        interactionUICustomUILists.flatten().first { it.getId() == id }.execute(playerUI)
        return null
    }

    override fun respondToInteraction(id: String, argument: String): Question? {
        interactionUICustomUILists.flatten().first { it.getId() == id }.executeWithArgument(playerUI, argument)
        return null
    }

    override fun getPlayerUI(): PlayerUI {
        return playerUI
    }

    override fun setPlayerUI(everyUI: PlayerUI): LongCustomUI {
        playerUI = everyUI
        return this
    }
}