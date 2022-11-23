package io.github.alexiscomete.lapinousecond.view.ui

interface InteractionUI {
    fun execute(ui: PlayerUI): InteractionUI
    fun executeWithArgument(ui: PlayerUI, argument: String): InteractionUI
    fun canBeExecutedWithArgument(): Boolean
    fun canBeExecutedWithoutArgument(): Boolean
    fun getId(): String
    fun setId(id: String): InteractionUI

}
