package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.PlayerUI

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

    override fun respondToInteraction(id: String): LongCustomUI {
        interactions.flatten().first { it.getId() == id }.execute(currentUI)
        return this
    }

    override fun respondToInteraction(id: String, argument: String): LongCustomUI {
        interactions.flatten().first { it.getId() == id }.executeWithArgument(currentUI, argument)
        return this
    }

    override fun getPlayerUI(): PlayerUI {
        return currentUI
    }
}