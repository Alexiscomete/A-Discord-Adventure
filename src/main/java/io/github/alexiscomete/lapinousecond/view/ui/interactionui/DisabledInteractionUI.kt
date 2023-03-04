package io.github.alexiscomete.lapinousecond.view.ui.interactionui

import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question

class DisabledInteractionUI(
    private var customUI: LongCustomUI,
    private val customInteractionStyle: InteractionStyle,
    private var id: String,
    private var title: String,
) : InteractionUICustomUI {

    override fun getCustomInteractionStyle(): InteractionStyle {
        return customInteractionStyle
    }

    override fun execute(ui: PlayerUI): Question? {
        return null
    }

    override fun executeWithArgument(ui: PlayerUI, argument: String): Question? {
        return null
    }

    override fun canBeExecutedWithArgument(): Boolean {
        return false
    }

    override fun canBeExecutedWithoutArgument(): Boolean {
        return false
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
        return null
    }

    override fun setDescription(description: String?): InteractionUI {
        return this
    }
}