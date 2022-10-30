package io.github.alexiscomete.lapinousecond.view.message_event

import io.github.alexiscomete.lapinousecond.view.Context
import org.javacord.api.event.interaction.ButtonClickEvent

class ButtonsContextManager(var hash: HashMap<String, (ButtonClickEvent, Context, ButtonsContextManager) -> Unit>) : ContextManager {
    override fun canApply(string: String): Boolean {
        return hash.containsKey(string)
    }
}