package io.github.alexiscomete.lapinousecond.message_event

import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.component.LowLevelComponent
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.MessageComponentCreateEvent
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

class MenuBuilder(name: String, description: String, color: Color) {

    private val embedBuilder: EmbedBuilder = EmbedBuilder()
        .setTitle(name)
        .setDescription(description)
        .setColor(color)

    private val arrayListOfButton = ArrayList<LowLevelComponent>()

    fun addButton(name: String, description: String, whenUsed: (MessageComponentCreateEvent) -> Unit): MenuBuilder {
        embedBuilder.addInlineField(name, description)

        val id = generateUniqueID()

        arrayListOfButton.add(Button.success(id.toString(), name))
        buttonsManager.addButton(id, whenUsed)

        return this
    }

    fun messageBuilder(): MessageBuilder {
        return MessageBuilder()
            .setEmbed(embedBuilder)
            .addComponents(ActionRow.of(arrayListOfButton))
    }

    fun responder(slashCommand: SlashCommandInteraction) {
        slashCommand.createImmediateResponder()
            .addEmbed(embedBuilder)
            .addComponents(ActionRow.of(arrayListOfButton))
            .respond()
    }

    fun modif(messageComponentCreateEvent: MessageComponentCreateEvent) {
        messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater()
            .removeAllEmbeds()
            .removeAllComponents()
            .addEmbed(embedBuilder)
            .addComponents(ActionRow.of(arrayListOfButton))
            .update()
    }
}