package io.github.alexiscomete.lapinousecond.view.contextmanager

import io.github.alexiscomete.lapinousecond.view.Context
import org.javacord.api.event.interaction.SelectMenuChooseEvent

abstract class SelectMenuContextManager(val name: String) : ContextManager {
    override fun canApply(string: String): Boolean {
        return string == name
    }

    abstract fun ex(smce: SelectMenuChooseEvent, c: Context)

    override fun toString(): String {
        return "SelectMenuContextManager{$name}"
    }
}
