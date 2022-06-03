package io.github.alexiscomete.lapinousecond

import io.github.alexiscomete.lapinousecond.entity.Player
import org.javacord.api.event.message.MessageCreateEvent

abstract class Item internal constructor(
    var name: String,
    var description: String,
    var price: Int,
    var jname: String,
    args: Array<String?>?
) {
    init {
        setArgs(args)
    }

    abstract fun setArgs(args: Array<String?>?)
    abstract fun use(event: MessageCreateEvent?, content: String?, args: Array<String?>?, ownerOfItem: Player?): Boolean
    abstract val args: String?
}