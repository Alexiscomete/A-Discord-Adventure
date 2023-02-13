package io.github.alexiscomete.lapinousecond.view.ui

class DisabledInteractionUI(
    private var customUI: LongCustomUI,
    private val customInteractionStyle: InteractionStyle,
    private var id: String,
    private var title: String,
) : InteractionUICustomUI {
    override fun getCustomUI(): LongCustomUI {
        return customUI
    }

    override fun setCustomUI(customUI: LongCustomUI): InteractionUICustomUI {
        this.customUI = customUI
        return this
    }

    override fun getCustomInteractionStyle(): InteractionStyle {
        return customInteractionStyle
    }

    override fun execute(ui: PlayerUI): InteractionUI {
        return this
    }

    override fun executeWithArgument(ui: PlayerUI, argument: String): InteractionUI {
        return this
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