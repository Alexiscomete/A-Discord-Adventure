package io.github.alexiscomete.lapinousecond.view.ui.interactionui

import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question

interface InteractionUI {
    fun execute(ui: PlayerUI): Question?
    fun executeWithArgument(ui: PlayerUI, argument: String): Question?
    fun canBeExecutedWithArgument(): Boolean
    fun canBeExecutedWithoutArgument(): Boolean
    fun getId(): String
    fun setId(id: String): InteractionUI
    fun getTitle(): String
    fun setTitle(title: String): InteractionUI
    fun getDescription(): String?
    fun setDescription(description: String?): InteractionUI
}
