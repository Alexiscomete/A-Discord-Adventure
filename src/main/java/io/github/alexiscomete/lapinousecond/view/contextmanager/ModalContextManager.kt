package io.github.alexiscomete.lapinousecond.view.contextmanager

import io.github.alexiscomete.lapinousecond.view.Context
import org.javacord.api.event.interaction.ModalSubmitEvent

@Deprecated("Use PlayerUI instead")
abstract class ModalContextManager(val name: String) : ContextManager {
    override fun canApply(string: String): Boolean {
        return string == name
    }

    abstract fun ex(smce: ModalSubmitEvent, c: Context)

    override fun toString(): String {
        return "ModalContextManager{$name}"
    }
}
