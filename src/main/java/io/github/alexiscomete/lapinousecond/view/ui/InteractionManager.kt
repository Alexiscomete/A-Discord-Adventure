package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID

class InteractionManager(
    val id: String = generateUniqueID().toString(),
    val title: String,
    val description: String? = null,
    val todoWhen: (PlayerUI) -> Unit,
    val todoWhenWithArgument: (PlayerUI, String) -> Unit,
) {
    fun execute(ui: PlayerUI) {
        todoWhen(ui)
    }

    fun executeWithArgument(ui: PlayerUI, argument: String) {
        todoWhenWithArgument(ui, argument)
    }
}