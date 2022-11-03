package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextmanager.ButtonsContextManager
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent

open class EmbedPages<U>(
    protected val builder: EmbedBuilder,
    protected val uArrayList: ArrayList<U>,
    protected val uAddContent: AddContent<U>,
    protected val context: Context
) {
    protected var level = 0
    protected val idLast = "last"
    protected val idNext = "next"
    protected val manager = ButtonsContextManager(
        hashMapOf(
            idLast to ::last,
            idNext to ::next
        )
    )
    open val number = 10

    protected open fun next(
        messageComponentCreateEvent: ButtonClickEvent,
        context: Context,
        manager: ButtonsContextManager
    ) {
        // check if the button is valid : it must have enough elements to go to the next page
        if (level + number < uArrayList.size) {
            level += number
            builder.removeAllFields()
            uAddContent.add(builder, level, number.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
                .removeAllComponents()
                .removeAllEmbeds()
                .addEmbed(builder)
                .addComponents(components)
                .update()
        }
    }

    protected open fun last(
        messageComponentCreateEvent: ButtonClickEvent,
        context: Context,
        manager: ButtonsContextManager
    ) {
        if (level > number - 1) {
            level -= number
            builder.removeAllFields()
            uAddContent.add(builder, level, number.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
                .removeAllComponents()
                .removeAllEmbeds()
                .addEmbed(builder)
                .addComponents(components)
                .update()
        }
    }

    open val components: ActionRow
        get() = if (level > 0 && level + number < uArrayList.size) {
            ActionRow.of(
                Button.success(idLast, "⬅ Page précédente"),
                Button.success(idNext, "Page suivante ➡")
            )
        } else if (level > 0) {
            ActionRow.of(
                Button.success(idLast, "⬅ Page précédente")
            )
        } else if (level + number < uArrayList.size) {
            ActionRow.of(
                Button.success(idNext, "Page suivante ➡")
            )
        } else {
            ActionRow.of(
                Button.success("0", "Aucune autre page", true)
            )
        }

    fun register() {
        context.buttons(manager)
    }

    init {
        uAddContent.add(builder, 0, number.coerceAtMost(uArrayList.size - level), uArrayList)
    }

    fun interface AddContent<U> {
        fun add(embedBuilder: EmbedBuilder, min: Int, num: Int, uArrayList: ArrayList<U>)
    }
}