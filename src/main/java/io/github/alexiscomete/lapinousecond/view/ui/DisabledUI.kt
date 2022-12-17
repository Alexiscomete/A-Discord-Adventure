package io.github.alexiscomete.lapinousecond.view.ui

class DisabledUI(
    private var customUI: LongCustomUI,
    private val customInteractionStyle: InteractionStyle,
    ) : InteractionUICustomUI {
    override fun getCustomUI(): LongCustomUI {
        return customUI
    }

    override fun setCustomUI(customUI: LongCustomUI): InteractionUICustomUI {
        this.customUI = customUI
        return this
    }

    override fun getCustomInteractionStyle(): InteractionStyle {
        TODO("Not yet implemented")
    }

    override fun execute(ui: PlayerUI): InteractionUI {
        TODO("Not yet implemented")
    }

    override fun executeWithArgument(ui: PlayerUI, argument: String): InteractionUI {
        TODO("Not yet implemented")
    }

    override fun canBeExecutedWithArgument(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canBeExecutedWithoutArgument(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getId(): String {
        TODO("Not yet implemented")
    }

    override fun setId(id: String): InteractionUI {
        TODO("Not yet implemented")
    }

    override fun getTitle(): String {
        TODO("Not yet implemented")
    }

    override fun setTitle(title: String): InteractionUI {
        TODO("Not yet implemented")
    }

    override fun getDescription(): String? {
        TODO("Not yet implemented")
    }

    override fun setDescription(description: String?): InteractionUI {
        TODO("Not yet implemented")
    }
}