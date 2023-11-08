package io.github.alexiscomete.lapinousecond.view.exceptions

import io.github.alexiscomete.lapinousecond.view.ViewType

abstract class ExceptionWithViewType : Exception() {
    abstract fun getMessageAdaptedForViewType(viewType: ViewType): String
}
