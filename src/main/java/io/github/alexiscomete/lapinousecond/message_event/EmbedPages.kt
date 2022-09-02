package io.github.alexiscomete.lapinousecond.message_event

import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent

open class EmbedPages<U>(
    protected val builder: EmbedBuilder,
    protected val uArrayList: ArrayList<U>,
    protected val uAddContent: AddContent<U>
) {
    protected var level = 0
    protected val idLast = generateUniqueID().toString()
    protected val idNext = generateUniqueID().toString()
    open val number = 10

    protected open fun next(messageComponentCreateEvent: ButtonClickEvent) {
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

    protected open fun last(messageComponentCreateEvent: ButtonClickEvent) {
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
        buttonsManager.addButton(idLast.toLong()) { messageComponentCreateEvent: ButtonClickEvent ->
                last(messageComponentCreateEvent)
            }
        buttonsManager.addButton(idNext.toLong()) { messageComponentCreateEvent: ButtonClickEvent ->
                next(messageComponentCreateEvent)
            }
    }

    init {
        uAddContent.add(builder, 0, number.coerceAtMost(uArrayList.size - level), uArrayList)
    }

    fun interface AddContent<U> {
        fun add(embedBuilder: EmbedBuilder, min: Int, num: Int, uArrayList: ArrayList<U>)
    }
}