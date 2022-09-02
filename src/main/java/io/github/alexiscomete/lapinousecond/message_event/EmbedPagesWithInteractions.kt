package io.github.alexiscomete.lapinousecond.message_event

import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.component.LowLevelComponent
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent

class EmbedPagesWithInteractions<U>(
    builder: EmbedBuilder,
    uArrayList: ArrayList<U>,
    uAddContent: AddContent<U>,
    val whenSelected: (U, ButtonClickEvent) -> Unit
) : EmbedPages<U>(
    builder,
    uArrayList,
    uAddContent
) {
    override val number = 5

    init {
        builder.setFooter("Cliquez sur les numéros pour interagir avec un élément")
    }

    val buttons: ArrayList<LowLevelComponent>
        get() {
            val buttons = ArrayList<LowLevelComponent>()
            for (i in level until level + number.coerceAtMost(uArrayList.size - level)) {
                buttons.add(component(uArrayList[i], i - level + 1))
            }
            return buttons
        }

    /**
     * It creates a button with a unique ID, adds it to the buttons manager, and returns it
     *
     * @param u U - The object that is being selected
     * @param index The index of the component in the list.
     * @return A Button
     */
    private fun component(u: U, index: Int): Button {
        println("Component $index")
        val id = generateUniqueID()
        buttonsManager.addButton(id) {
            whenSelected(u, it)
        }
        return Button.success(id.toString(), index.toString())
    }

    override fun next(messageComponentCreateEvent: ButtonClickEvent) {
        if (level + number < uArrayList.size) {
            level += number
            builder.removeAllFields()
            uAddContent.add(builder, level, number.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
                .removeAllComponents()
                .removeAllEmbeds()
                .addEmbed(builder)
                .addComponents(components, ActionRow.of(buttons))
                .update()
        }
    }

    override fun last(messageComponentCreateEvent: ButtonClickEvent) {
        if (level > number - 1) {
            level -= number
            builder.removeAllFields()
            uAddContent.add(builder, level, number.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
                .removeAllComponents()
                .removeAllEmbeds()
                .addEmbed(builder)
                .addComponents(components, ActionRow.of(buttons))
                .update()
        }
    }

}