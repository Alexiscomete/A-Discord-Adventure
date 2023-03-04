package io.github.alexiscomete.lapinousecond.view.ui.interactionui

import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI

interface InteractionUICustomUI : InteractionUI {
    fun getCustomInteractionStyle(): InteractionStyle
}