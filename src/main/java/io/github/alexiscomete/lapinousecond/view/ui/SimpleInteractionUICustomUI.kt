package io.github.alexiscomete.lapinousecond.view.ui

class SimpleInteractionUICustomUI(
    private var id: String,
    private var title: String,
    private var description: String?,
    private var customUI: LongCustomUI,
    private val customInteractionStyle: InteractionStyle,
    private var executeWithoutArg: ((PlayerUI) -> Unit)?,
    private var executeWithArg: ((PlayerUI, String) -> Unit)?
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
        if (canBeExecutedWithoutArgument()) {
            executeWithoutArg?.invoke(ui)
        }
        return this
    }

    override fun executeWithArgument(ui: PlayerUI, argument: String): InteractionUI {
        if (canBeExecutedWithArgument()) {
            executeWithArg?.invoke(ui, argument)
        }
        return this
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