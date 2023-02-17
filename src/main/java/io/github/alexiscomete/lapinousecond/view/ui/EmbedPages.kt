package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.view.contextmanager.ButtonsContextManager
import io.github.alexiscomete.lapinousecond.view.ui.longuis.BaseUI
import java.awt.image.BufferedImage

open class EmbedPages<U>(
    linkedImage: String?,
    bufferedImage: BufferedImage?,
    title: String?,
    description: String?,
    underString: String?,
    protected val uArrayList: ArrayList<U>,
    protected val uAddContent: AddContent<U>,
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
    protected var level = 0
    private val idLast = "last"
    private val idNext = "next"
    protected val manager = ButtonsContextManager(
        hashMapOf(
            idLast to ::last,
            idNext to ::next
        )
    )
    open val number = 10

    init {
        uAddContent.add(0, number.coerceAtMost(uArrayList.size - level), uArrayList)
    }

    protected open fun next(
        playerUI: PlayerUI
    ) {
        // check if the button is valid : it must have enough elements to go to the next page
        if (level + number < uArrayList.size) {
            level += number
            uAddContent.add(level, number.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
                .removeAllComponents()
                .removeAllEmbeds()
                .addEmbed(builder)
                .addComponents(components)
                .update()
        }
    }

    protected open fun last(
        playerUI: PlayerUI
    ) {
        if (level > number - 1) {
            level -= number
            uAddContent.add(level, number.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
                .removeAllComponents()
                .removeAllEmbeds()
                .addEmbed(builder)
                .addComponents(components)
                .update()
        }
    }

    // TODO
    open val components: List<InteractionUICustomUI>
        get() =
            if (level > 0 && level + number < uArrayList.size) {
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
            } else if (level > 0) {
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
            } else if (level + number < uArrayList.size) {
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
        TODO("Not yet implemented")
    }

    fun interface AddContent<U> {
        fun add(min: Int, num: Int, uArrayList: ArrayList<U>)
    }
}