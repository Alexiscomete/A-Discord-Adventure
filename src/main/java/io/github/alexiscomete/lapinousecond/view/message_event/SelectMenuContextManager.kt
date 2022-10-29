package io.github.alexiscomete.lapinousecond.view.message_event

import io.github.alexiscomete.lapinousecond.view.Context
import org.javacord.api.event.interaction.SelectMenuChooseEvent

class SelectMenuContextManager(val name: String, val ex: ((SelectMenuChooseEvent, Context) -> Unit)) : ContextManager {
    override fun canApply(string: String): Boolean {
        return string == name
    }
}
