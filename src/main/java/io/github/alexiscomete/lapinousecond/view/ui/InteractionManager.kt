package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID

class InteractionManager(
    val id: String = generateUniqueID().toString(),
    val title: String,
    val description: String? = null,
    val interactionUI: InteractionUI,
    val todoWhen: (PlayerUI) -> Unit = {
        interactionUI.execute(it)
    },
    val todoWhenWithArgument: (PlayerUI, String) -> Unit = { ui, argument ->
        interactionUI.executeWithArgument(ui, argument)
    }
) {
    fun execute(ui: PlayerUI) {
        todoWhen(ui)
    }

    fun executeWithArgument(ui: PlayerUI, argument: String) {
        todoWhenWithArgument(ui, argument)
    }
}