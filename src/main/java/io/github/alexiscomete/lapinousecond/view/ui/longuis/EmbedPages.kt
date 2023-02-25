package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.DisabledInteractionUI
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import java.awt.image.BufferedImage

open class EmbedPages<U>(
    linkedImage: String?,
    bufferedImage: BufferedImage?,
    title: String?,
    description: String?,
    underString: String?,
    protected val uArrayList: ArrayList<U>,
    protected val uContentOf: ContentOf<U>,
    context: PlayerUI
) : BaseUI(
    linkedImage,
    bufferedImage,
    title,
    description,
    underString,
    listOf(),
    context
) {
    protected var pageLevel = 0
    private val idLast = "last"
    private val idNext = "next"
    open val number = 10

    protected open fun next(
        playerUI: PlayerUI
    ) {
        // check if the button is valid : it must have enough elements to go to the next page
        if (pageLevel + number < uArrayList.size) {
            pageLevel += number
            setInteractionUICustomUIs(listOf(components))
        }
    }

    protected open fun last(
        playerUI: PlayerUI
    ) {
        if (pageLevel > number - 1) {
            pageLevel -= number
            setInteractionUICustomUIs(listOf(components))
        }
    }

    open val components: List<InteractionUICustomUI>
        get() =
            if (pageLevel > 0 && pageLevel + number < uArrayList.size) {
                listOf(
                    SimpleInteractionUICustomUI(
                        idLast,
                        "⬅ Page précédente",
                        "Page suivant dans le menu",
                        this,
                        InteractionStyle.NORMAL,
                        ::last,
                        null
                    ),
                    SimpleInteractionUICustomUI(
                        idNext,
                        "Page suivante ➡",
                        "Page suivant dans le menu",
                        this,
                        InteractionStyle.NORMAL,
                        ::next,
                        null
                    )
                )
            } else if (pageLevel > 0) {
                listOf(
                    SimpleInteractionUICustomUI(
                        idLast,
                        "⬅ Page précédente",
                        "Page suivant dans le menu",
                        this,
                        InteractionStyle.NORMAL,
                        ::last,
                        null
                    )
                )
            } else if (pageLevel + number < uArrayList.size) {
                listOf(
                    SimpleInteractionUICustomUI(
                        idNext,
                        "Page suivante ➡",
                        "Page suivant dans le menu",
                        this,
                        InteractionStyle.NORMAL,
                        ::next,
                        null
                    )
                )
            } else {

                listOf(
                    DisabledInteractionUI(
                        this,
                        InteractionStyle.NORMAL_DISABLED,
                        "0",
                        "Aucune autre page"
                    )
                )

            }


    fun register() {
        TODO("Not yet implemented")
    }

    override fun getFields(): List<Pair<String, String>>? {
        return uContentOf.getContent(pageLevel, number.coerceAtMost(uArrayList.size - pageLevel), uArrayList)
    }

    fun interface ContentOf<U> {
        fun getContent(min: Int, num: Int, uArrayList: ArrayList<U>): List<Pair<String, String>>?
    }
}