package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextmanager.ButtonsContextManager
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.component.LowLevelComponent
import org.javacord.api.event.interaction.ButtonClickEvent
import java.awt.image.BufferedImage

class EmbedPagesWithInteractions<U>(
    uArrayList: ArrayList<U>,
    uAddContent: AddContent<U>,
    val whenSelected: (U, ButtonClickEvent, Context) -> Unit,
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
    uAddContent,
    context
) {
    override val number = 5

    init {
        uAddContent.add(0, number.coerceAtMost(uArrayList.size - level), uArrayList)
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
        //TODO
        manager.hash[index.toString()] = { event, context, _ ->
            whenSelected(u, event, context)
        }
        return Button.success(index.toString(), index.toString())
    }

    override fun next(messageComponentCreateEvent: ButtonClickEvent, context: Context, manager: ButtonsContextManager) {
        if (level + number < uArrayList.size) {
            level += number
            uAddContent.add(builder, level, number.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
                .removeAllComponents()
                .removeAllEmbeds()
                .addEmbed(builder)
                .addComponents(components, ActionRow.of(buttons))
                .update()
        }
    }

    override fun last(messageComponentCreateEvent: ButtonClickEvent, context: Context, manager: ButtonsContextManager) {
        if (level > number - 1) {
            level -= number
            uAddContent.add(level, number.coerceAtMost(uArrayList.size - level), uArrayList)
            messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
                .removeAllComponents()
                .removeAllEmbeds()
                .addEmbed(builder)
                .addComponents(components, ActionRow.of(buttons))
                .update()
        }
    }

}