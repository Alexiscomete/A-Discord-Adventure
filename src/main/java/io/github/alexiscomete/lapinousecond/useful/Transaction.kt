package io.github.alexiscomete.lapinousecond.useful

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import org.javacord.api.entity.channel.TextChannel
import java.util.function.Consumer

open class Transaction(
    private val addMoney: (Double) -> Unit,
    private val removeMoney: (Double) -> Unit,
    private val getMoney: () -> Double
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
            acc(player.getAnswer(AnswerEnum.TR_END, true))
        } else {
            de(
                player.getAnswer(
                    AnswerEnum.ECHEC_TRANS, true, player.getAnswer(AnswerEnum.NO_ENOUGH_MONEY, false)
                )
            )
        }
    }

    private fun basic(quantity: Double): Boolean {
        val money = getMoney()
        return if (money > quantity) {
            addMoney(quantity)
            removeMoney(quantity)
            true
        } else {
            false
        }
    }
}