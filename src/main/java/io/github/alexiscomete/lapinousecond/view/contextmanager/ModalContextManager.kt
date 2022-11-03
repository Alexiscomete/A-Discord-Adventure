package io.github.alexiscomete.lapinousecond.view.contextmanager

import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextmanager.ContextManager
import org.javacord.api.event.interaction.ModalSubmitEvent

abstract class ModalContextManager(val name: String) : ContextManager {
    override fun canApply(string: String): Boolean {
        return string == name
    }

    abstract fun ex(smce: ModalSubmitEvent, c: Context)
}
