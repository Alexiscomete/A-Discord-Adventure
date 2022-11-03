package io.github.alexiscomete.lapinousecond.view.contextmanager

interface ContextManager {
    fun canApply(string: String): Boolean
}