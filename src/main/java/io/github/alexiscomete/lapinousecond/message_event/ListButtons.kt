package io.github.alexiscomete.lapinousecond.message_event

import io.github.alexiscomete.lapinousecond.ListenerMain
import io.github.alexiscomete.lapinousecond.Main
import io.github.alexiscomete.lapinousecond.save.SaveLocation.Companion.generateUniqueID
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
            uAddContent.add(builder, level, Math.min(10, uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater().removeAllEmbeds()
                .addEmbed(builder).addComponents(
                components
            ).update()
        }
    }

    fun last(messageComponentCreateEvent: MessageComponentCreateEvent) {
        if (level > 9) {
            level -= 10
            builder.removeAllFields()
            uAddContent.add(builder, level, Math.min(10, uArrayList.size - level), uArrayList)
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
        Main.buttonsManager
            .addButton(idLast.toLong()) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                this.last(messageComponentCreateEvent)
            }
        Main.buttonsManager
            .addButton(idNext.toLong()) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                next(messageComponentCreateEvent)
            }
    }

    init {
        uAddContent.add(builder, 0, Math.min(10, uArrayList.size - level), uArrayList)
    }

    fun interface AddContent<U> {
        fun add(embedBuilder: EmbedBuilder?, min: Int, num: Int, uArrayList: ArrayList<U>?)
    }
}