package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.InteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.PlayerUI

abstract class BaseRespondUI(
    protected var interactionUICustomUILists: List<List<InteractionUICustomUI>>,
    protected var playerUI: PlayerUI
) : LongCustomUI {
    override fun getInteractionUICustomUILists(): List<List<InteractionUICustomUI>> {
        return interactionUICustomUILists
    }

    override fun hasInteractionID(id: String): Boolean {
        return interactionUICustomUILists.flatten().any { it.getId() == id }
    }

    override fun respondToInteraction(id: String): LongCustomUI {
        interactionUICustomUILists.flatten().first { it.getId() == id }.execute(playerUI)
        return this
    }

    override fun respondToInteraction(id: String, argument: String): LongCustomUI {
        interactionUICustomUILists.flatten().first { it.getId() == id }.executeWithArgument(playerUI, argument)
        return this
    }

    override fun getPlayerUI(): PlayerUI {
        return playerUI
    }
}