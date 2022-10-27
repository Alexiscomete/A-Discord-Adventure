package io.github.alexiscomete.lapinousecond.view.message_event

import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.component.LowLevelComponent
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.interaction.ModalInteraction
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color
import java.awt.image.BufferedImage

class MenuBuilder(name: String, description: String, color: Color, val player: Long) {

    private var ephemeral = false

    private val embedBuilder: EmbedBuilder = EmbedBuilder()
        .setTitle(name)
        .setDescription(description)
        .setColor(color)

    private val arrayListOfButton = ArrayList<LowLevelComponent>()

    fun addButton(name: String, description: String, whenUsed: (ButtonClickEvent) -> Unit): MenuBuilder {
        embedBuilder.addInlineField(name, description)

        val id = generateUniqueID()

        arrayListOfButton.add(Button.success(id.toString(), name))
        buttonsManager.addButton(id) { event ->
            if (event.buttonInteraction.user.id == player) {
                whenUsed(event)
            }
        }

        return this
    }

    fun addEphemeral() : MenuBuilder {
        ephemeral = true
        return this
    }

    fun setImage(image: BufferedImage): MenuBuilder {
        embedBuilder.setImage(image)
        return this
    }

    fun messageBuilder(): MessageBuilder {
        return MessageBuilder()
            .setEmbed(embedBuilder)
            .addComponents(ActionRow.of(arrayListOfButton))
    }

    fun responder(slashCommand: SlashCommandInteraction) {
        val responder = slashCommand.createImmediateResponder()
            .addEmbed(embedBuilder)
            .addComponents(ActionRow.of(arrayListOfButton))
        if (ephemeral) responder.setFlags(MessageFlag.EPHEMERAL)
        responder.respond()
    }

    fun responder(modalInteraction: ModalInteraction) {
        val responder = modalInteraction.createImmediateResponder()
            .addEmbed(embedBuilder)
            .addComponents(ActionRow.of(arrayListOfButton))
        if (ephemeral) responder.setFlags(MessageFlag.EPHEMERAL)
        responder.respond()
    }

    fun modif(messageComponentCreateEvent: ButtonClickEvent) {
        val updater = messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
            .removeAllEmbeds()
            .removeAllComponents()
            .addEmbed(embedBuilder)
            .addComponents(ActionRow.of(arrayListOfButton))
        if (ephemeral) updater.setFlags(MessageFlag.EPHEMERAL)
        updater.update()
    }

    fun modif(messageComponentCreateEvent: SelectMenuChooseEvent) {
        val updater = messageComponentCreateEvent.selectMenuInteraction.createOriginalMessageUpdater()
            .removeAllEmbeds()
            .removeAllComponents()
            .addEmbed(embedBuilder)
            .addComponents(ActionRow.of(arrayListOfButton))
        if (ephemeral) updater.setFlags(MessageFlag.EPHEMERAL)
        updater.update()
    }
}