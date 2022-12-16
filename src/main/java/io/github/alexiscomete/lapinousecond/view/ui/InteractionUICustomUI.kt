package io.github.alexiscomete.lapinousecond.view.ui

interface InteractionUICustomUI : InteractionUI {
    fun getCustomUI(): LongCustomUI
    fun setCustomUI(customUI: LongCustomUI): InteractionUICustomUI
    fun getCustomInteractionStyle(): InteractionStyle
}