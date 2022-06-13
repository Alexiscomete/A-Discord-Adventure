package io.github.alexiscomete.lapinousecond.useful

import io.github.alexiscomete.lapinousecond.*
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.interaction.MessageComponentInteraction
import java.util.function.Consumer
import java.util.function.Supplier

open class FullTransaction(
    addMoney: Consumer<Double>,
    removeMoney: Consumer<Double>,
    getMoney: Supplier<Double>,
    p: Player,
    private val max: Supplier<Double>
) : VerifTransaction(addMoney, removeMoney, getMoney, p) {

    private fun askQuantity(after: Consumer<Double>, textChannel: TextChannel) {
        textChannel.sendMessage(p.getAnswer(AnswerEnum.ASK_MONTANT, true))
        addL(textChannel, after)
    }

    private fun askQuantity(after: Consumer<Double>, messageComponentInteraction: MessageComponentInteraction) {
        messageComponentInteraction.createImmediateResponder().setContent(p.getAnswer(AnswerEnum.ASK_MONTANT, true))
            .respond()
        addL(messageComponentInteraction.channel.get(), after)
    }

    private fun addL(textChannel: TextChannel, after: Consumer<Double>) {
        messagesManager.addListener(textChannel, p.id) { messageCreateEvent: MessageCreateEvent ->
            try {
                val d = messageCreateEvent.message.content.toDouble()
                if (d > max.get()) {
                    messageCreateEvent.message.reply(p.getAnswer(AnswerEnum.VALUE_TOO_HIGH, true, max))
                    addL(textChannel, after)
                }
                after.accept(d)
            } catch (e: IllegalArgumentException) {
                messageCreateEvent.message.reply(p.getAnswer(AnswerEnum.FORM_INVALID, true))
                addL(textChannel, after)
            }
        }
    }

    fun full(textChannel: TextChannel) {
        askQuantity({ aDouble: Double? -> askVerif(aDouble!!, textChannel) }, textChannel)
    }

    fun full(messageComponentInteraction: MessageComponentInteraction) {
        askQuantity(
            { aDouble: Double? -> askVerif(aDouble!!, messageComponentInteraction.channel.get()) },
            messageComponentInteraction
        )
    }
}