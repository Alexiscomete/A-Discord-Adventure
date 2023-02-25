package io.github.alexiscomete.lapinousecond.view.ui.interactionui

import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI

class InteractionManager(
    private var id: String = generateUniqueID().toString(),
    private var title: String,
    private var description: String? = null,
    val todoWhen: (PlayerUI) -> Unit,
    val todoWhenWithArgument: (PlayerUI, String) -> Unit
) : InteractionUI {
    override fun execute(ui: PlayerUI): InteractionUI {
        todoWhen(ui)
        return this
    }

    override fun executeWithArgument(ui: PlayerUI, argument: String): InteractionUI {
        todoWhenWithArgument(ui, argument)
        return this
    }

    override fun canBeExecutedWithArgument(): Boolean {
        return true
    }

    override fun canBeExecutedWithoutArgument(): Boolean {
        return true
    }

    override fun getId(): String {
        return id
    }

    override fun setId(id: String): InteractionUI {
        this.id = id
        return this
    }

    override fun getTitle(): String {
        return title
    }

    override fun setTitle(title: String): InteractionUI {
        this.title = title
        return this
    }

    override fun getDescription(): String? {
        return description
    }

    override fun setDescription(description: String?): InteractionUI {
        this.description = description
        return this
    }
}