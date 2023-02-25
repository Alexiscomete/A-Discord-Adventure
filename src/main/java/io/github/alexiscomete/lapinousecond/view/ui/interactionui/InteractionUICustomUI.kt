package io.github.alexiscomete.lapinousecond.view.ui.interactionui

import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI

interface InteractionUICustomUI : InteractionUI {
    fun getCustomUI(): LongCustomUI
    fun setCustomUI(customUI: LongCustomUI): InteractionUICustomUI
    fun getCustomInteractionStyle(): InteractionStyle
}