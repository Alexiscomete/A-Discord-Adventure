package io.github.alexiscomete.lapinousecond.view.ui.interactionui

import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question

class SimpleInteractionUICustomUI(
    private var id: String,
    private var title: String,
    private var description: String?,
    private val customInteractionStyle: InteractionStyle,
    private var executeWithoutArg: ((PlayerUI) -> Question?)?,
    private var executeWithArg: ((PlayerUI, String) -> Question?)? = null
) : InteractionUICustomUI {

    override fun getCustomInteractionStyle(): InteractionStyle {
        return customInteractionStyle
    }

    override fun execute(ui: PlayerUI): Question? {
        if (canBeExecutedWithoutArgument()) {
            return executeWithoutArg?.invoke(ui)
        }
        return null
    }

    override fun executeWithArgument(ui: PlayerUI, argument: String): Question? {
        if (canBeExecutedWithArgument()) {
            return executeWithArg?.invoke(ui, argument)
        }
        return null
    }

    override fun canBeExecutedWithArgument(): Boolean {
        return executeWithArg != null
    }

    override fun canBeExecutedWithoutArgument(): Boolean {
        return executeWithoutArg != null
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