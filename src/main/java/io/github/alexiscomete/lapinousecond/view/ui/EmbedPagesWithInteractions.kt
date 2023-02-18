package io.github.alexiscomete.lapinousecond.view.ui

import java.awt.image.BufferedImage

class EmbedPagesWithInteractions<U>(
    uArrayList: ArrayList<U>,
    uContentOf: ContentOf<U>,
    val whenSelected: (U, PlayerUI) -> Unit,
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
    override val number = 5

    val buttons: List<InteractionUICustomUI>
        get() {
            val buttons = mutableListOf<InteractionUICustomUI>()
            for (i in pageLevel until pageLevel + number.coerceAtMost(uArrayList.size - pageLevel)) {
                buttons.add(
                    SimpleInteractionUICustomUI(
                        (i - pageLevel + 1).toString(),
                        (i - pageLevel + 1).toString(),
                        "Cliquez pour interagir avec l'élément n°${i + 1} de la liste",
                        this,
                        InteractionStyle.NORMAL,
                        { ui -> whenSelected(uArrayList[i], ui) },
                        null
                    )
                )
            }
            return buttons
        }

    override fun next(playerUI: PlayerUI) {
        if (pageLevel + number < uArrayList.size) {
            pageLevel += number
            uContentOf.getContent(pageLevel, number.coerceAtMost(uArrayList.size - pageLevel), uArrayList)
        }
    }

    override fun last(playerUI: PlayerUI) {
        if (pageLevel > number - 1) {
            pageLevel -= number
            uContentOf.getContent(pageLevel, number.coerceAtMost(uArrayList.size - pageLevel), uArrayList)
        }
    }

}