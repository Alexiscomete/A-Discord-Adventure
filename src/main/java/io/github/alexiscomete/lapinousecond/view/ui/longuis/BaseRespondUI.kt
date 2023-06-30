package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question

abstract class BaseRespondUI(
    protected var interactions: List<List<InteractionUICustomUI>>,
    protected var currentUI: PlayerUI
) : LongCustomUI {
    override fun getInteractionUICustomUILists(): List<List<InteractionUICustomUI>> {
        return interactions
    }

    override fun hasInteractionID(id: String): Boolean {
        return interactions.flatten().any { it.getId() == id }
    }

    override fun respondToInteraction(id: String): Question? {
        return interactions.flatten().first { it.getId() == id }.execute(currentUI)
    }

    override fun respondToInteraction(id: String, argument: String): Question? {
        return interactions.flatten().first { it.getId() == id }.executeWithArgument(currentUI, argument)
    }

    override fun getPlayerUI(): PlayerUI {
        return currentUI
    }
}