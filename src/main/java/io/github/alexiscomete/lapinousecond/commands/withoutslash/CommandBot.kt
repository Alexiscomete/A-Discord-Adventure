package io.github.alexiscomete.lapinousecond.commands.withoutslash

import io.github.alexiscomete.lapinousecond.UserPerms
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import org.javacord.api.event.message.MessageCreateEvent
import io.github.alexiscomete.lapinousecond.commands.withoutslash.classes.*

abstract class CommandBot(
    val description: String,
    val name: String,
    val totalDescription: String,
    vararg perms: String
) {
    @JvmField
    val perms: Array<out String>

    init {
        this.perms = perms
    }

    fun checkAndExecute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {}

    abstract fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>)

    fun sendImpossible(messageCreateEvent: MessageCreateEvent, p: Player) {
        messageCreateEvent.message.reply(p.getAnswer(AnswerEnum.IMP_SIT, true))
    }

    fun sendArgs(messageCreateEvent: MessageCreateEvent, p: Player) {
        messageCreateEvent.message.reply(p.getAnswer(AnswerEnum.NO_ENOUGH_ARGS, true))
    }

    fun sendNumberEx(messageCreateEvent: MessageCreateEvent, p: Player, i: Int) {
        messageCreateEvent.message.reply(p.getAnswer(AnswerEnum.ILLEGAL_ARGUMENT_NUMBER, true, arrayListOf(i.toString())))
    }

    fun isNotNumeric(str: String): Boolean {
        return try {
            str.toInt()
            false
        } catch (e: NumberFormatException) {
            true
        }
    }
}