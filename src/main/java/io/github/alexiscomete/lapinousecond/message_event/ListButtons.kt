package io.github.alexiscomete.lapinousecond.message_event

import io.github.alexiscomete.lapinousecond.ListenerMain
import io.github.alexiscomete.lapinousecond.*
import io.github.alexiscomete.lapinousecond.save.saveManager
import io.github.alexiscomete.lapinousecond.save.*
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.MessageComponentCreateEvent

class ListButtons<U>(
    private val builder: EmbedBuilder,
    private val uArrayList: ArrayList<U>,
    private val uAddContent: AddContent<U>
) {
    private var level = 0
    private val idLast = generateUniqueID().toString()
    private val idNext = generateUniqueID().toString()
    fun next(messageComponentCreateEvent: MessageComponentCreateEvent) {
        if (level + 10 < uArrayList.size) {
            level += 10
            builder.removeAllFields()
            uAddContent.add(builder, level, 10.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater().removeAllEmbeds()
                .addEmbed(builder).addComponents(
                components
            ).update()
        }
    }

    private fun last(messageComponentCreateEvent: MessageComponentCreateEvent) {
        if (level > 9) {
            level -= 10
            builder.removeAllFields()
            uAddContent.add(builder, level, 10.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater().removeAllEmbeds()
                .addEmbed(builder).addComponents(
                components
            ).update()
        }
    }

    val components: ActionRow
        get() = if (level > 0 && level + 10 < ListenerMain.commands.size) {
            ActionRow.of(
                Button.success(idLast, "Page précédente"),
                Button.success(idNext, "Page suivante")
            )
        } else if (level > 0) {
            ActionRow.of(
                Button.success(idLast, "Page précédente")
            )
        } else if (level + 10 < ListenerMain.commands.size) {
            ActionRow.of(
                Button.success(idNext, "Page suivante")
            )
        } else {
            ActionRow.of()
        }

    fun register() {
        buttonsManager
            .addButton(idLast.toLong()) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                this.last(messageComponentCreateEvent)
            }
        buttonsManager
            .addButton(idNext.toLong()) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                next(messageComponentCreateEvent)
            }
    }

    init {
        uAddContent.add(builder, 0, 10.coerceAtMost(uArrayList.size - level), uArrayList)
    }

    fun interface AddContent<U> {
        fun add(embedBuilder: EmbedBuilder, min: Int, num: Int, uArrayList: ArrayList<U>)
    }
}