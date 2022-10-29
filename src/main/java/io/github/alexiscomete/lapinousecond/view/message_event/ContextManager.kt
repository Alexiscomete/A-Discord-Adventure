package io.github.alexiscomete.lapinousecond.view.message_event

interface ContextManager {
    fun canApply(string: String): Boolean
}