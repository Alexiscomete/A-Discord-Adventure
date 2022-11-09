package io.github.alexiscomete.lapinousecond.view.contextmanager

import io.github.alexiscomete.lapinousecond.view.Context
import org.javacord.api.event.interaction.ButtonClickEvent

class ButtonsContextManager(var hash: HashMap<String, (ButtonClickEvent, Context, ButtonsContextManager) -> Unit>) :
    ContextManager {
    override fun canApply(string: String): Boolean {
        return hash.containsKey(string)
    }

    override fun toString(): String {
        var string = "ButtonsContextManager{"
        for (key in hash.keys) {
            string += "$key, "
        }
        return string.substring(0, string.length - 2) + "}"
    }
}