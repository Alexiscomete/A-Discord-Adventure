package io.github.alexiscomete.lapinousecond.useful

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import org.javacord.api.entity.channel.TextChannel
import java.util.function.Consumer
import java.util.function.Supplier

open class Transaction(
    private val addMoney: Consumer<Double>,
    private val removeMoney: Consumer<Double>,
    private val getMoney: Supplier<Double>
) {
    fun make(textChannel: TextChannel, quantity: Double, player: Player) {
        make(quantity, player, { content: String? -> textChannel.sendMessage(content) }) { content: String? ->
            textChannel.sendMessage(
                content
            )
        }
    }

    fun make(quantity: Double, player: Player, acc: (String) -> Unit, de: (String) -> Unit) {
        if (basic(quantity)) {
            player.getAnswer(AnswerEnum.TR_END, true)?.let { acc(it) }
        } else {
            player.getAnswer(
                AnswerEnum.ECHEC_TRANS, true, player.getAnswer(AnswerEnum.NO_ENOUGH_MONEY, false)
            )?.let {
                de(
                    it
                )
            }
        }
    }

    private fun basic(quantity: Double): Boolean {
        val money = getMoney.get()
        return if (money > quantity) {
            addMoney.accept(quantity)
            removeMoney.accept(quantity)
            true
        } else {
            false
        }
    }
}