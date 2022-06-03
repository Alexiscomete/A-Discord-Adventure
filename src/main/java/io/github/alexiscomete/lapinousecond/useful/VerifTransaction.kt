package io.github.alexiscomete.lapinousecond.useful

import io.github.alexiscomete.lapinousecond.Main
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.save.SaveLocation
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.MessageComponentCreateEvent
import java.awt.Color
import java.util.function.Consumer
import java.util.function.Supplier

open class VerifTransaction(
    addMoney: Consumer<Double>,
    removeMoney: Consumer<Double>,
    getMoney: Supplier<Double>,
    val p: Player
) : Transaction(addMoney, removeMoney, getMoney) {
    fun askVerif(quantity: Double, textChannel: TextChannel?) {
        val id = SaveLocation.generateUniqueID()
        val embedBuilder = EmbedBuilder()
            .setTitle(p.getAnswer(AnswerEnum.CONFIRMATION, true))
            .setColor(Color.BLUE)
            .setDescription(p.getAnswer(AnswerEnum.MONTANT_TR, true, quantity.toString()))
        val messageBuilder = MessageBuilder()
            .addEmbed(embedBuilder)
            .addComponents(ActionRow.of(Button.success(id.toString(), "Valider")))
        messageBuilder.send(textChannel)
        Main.buttonsManager.addButton(id) { messageComponentCreateEvent: MessageComponentCreateEvent ->
            if (messageComponentCreateEvent.messageComponentInteraction.user.id == p.id) {
                make(
                    quantity,
                    p,
                    { str: String? ->
                        messageComponentCreateEvent.messageComponentInteraction.createImmediateResponder()
                            .setContent(str).respond()
                    }) { str: String? ->
                    messageComponentCreateEvent.messageComponentInteraction.createImmediateResponder().setContent(str)
                        .respond()
                }
            }
        }
    }
}