package io.github.alexiscomete.lapinousecond.view.ui.interactionui

import io.github.alexiscomete.lapinousecond.data.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question

class InteractionManager(
    private var id: String = generateUniqueID().toString(),
    private var title: String,
    private var description: String? = null,
    val todoWhen: (PlayerUI) -> Question?,
    val todoWhenWithArgument: (PlayerUI, String) -> Question?
) : InteractionUI {
    override fun execute(ui: PlayerUI): Question? {
        return todoWhen(ui)
    }

    override fun executeWithArgument(ui: PlayerUI, argument: String): Question? {
        return todoWhenWithArgument(ui, argument)
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