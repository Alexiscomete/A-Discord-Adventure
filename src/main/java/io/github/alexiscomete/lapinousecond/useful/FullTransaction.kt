package io.github.alexiscomete.lapinousecond.useful

import io.github.alexiscomete.lapinousecond.Main
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.interaction.MessageComponentInteraction
import java.util.function.Consumer
import java.util.function.Supplier

class FullTransaction : VerifTransaction {
    private val max: Supplier<Double>?

    constructor(
        addMoney: Consumer<Double>,
        removeMoney: Consumer<Double>,
        getMoney: Supplier<Double>,
        p: Player,
        max: Supplier<Double>
    ) : super(addMoney, removeMoney, getMoney, p) {
        this.max = max
    }

    constructor(
        addMoney: Consumer<Double>,
        removeMoney: Consumer<Double>,
        getMoney: Supplier<Double>,
        p: Player
    ) : super(addMoney, removeMoney, getMoney, p) {
        max = null
    }

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
        Main.getMessagesManager().addListener(textChannel, p.id) { messageCreateEvent: MessageCreateEvent ->
            try {
                val d = messageCreateEvent.message.content.toDouble()
                if (max != null && d > max.get()) {
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