package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question
import java.awt.image.BufferedImage

open class EmbedPagesWithInteractions<U>(
    uArrayList: ArrayList<U>,
    uContentOf: (min: Int, num: Int, uArrayList: ArrayList<U>) -> List<Pair<String, String>>,
    val whenSelected: (U, PlayerUI) -> Question?,
    linkedImage: String?,
    bufferedImage: BufferedImage?,
    title: String?,
    description: String?,
    context: PlayerUI
) : EmbedPages<U>(
    linkedImage,
    bufferedImage,
    title,
    description,
    "Cliquez sur les numéros pour interagir avec un élément",
    uArrayList,
    uContentOf,
    context
) {
    override val elementsPerPages = 5

    override fun addComponents() {
        setInteractionUICustomUIs(
            listOf(
                buttons,
                components
            )
        )
    }

    protected val buttons: List<InteractionUICustomUI>
        get() {
            val buttons = mutableListOf<InteractionUICustomUI>()
            for (i in pageLevel until pageLevel + elementsPerPages.coerceAtMost(uArrayList.size - pageLevel)) {
                buttons.add(
                    SimpleInteractionUICustomUI(
                        (i - pageLevel + 1).toString(),
                        (i - pageLevel + 1).toString(),
                        "Cliquez pour interagir avec l'élément n°${i + 1} de la liste",
                        InteractionStyle.NORMAL,
                        { ui -> whenSelected(uArrayList[i], ui) },
                        null
                    )
                )
            }
            return buttons
        }

}