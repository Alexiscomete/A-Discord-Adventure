package io.github.alexiscomete.lapinousecond.view.ui.interactionui

import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI

interface InteractionUI {
    fun execute(ui: PlayerUI): InteractionUI
    fun executeWithArgument(ui: PlayerUI, argument: String): InteractionUI
    fun canBeExecutedWithArgument(): Boolean
    fun canBeExecutedWithoutArgument(): Boolean
    fun getId(): String
    fun setId(id: String): InteractionUI
    fun getTitle(): String
    fun setTitle(title: String): InteractionUI
    fun getDescription(): String?
    fun setDescription(description: String?): InteractionUI
}
