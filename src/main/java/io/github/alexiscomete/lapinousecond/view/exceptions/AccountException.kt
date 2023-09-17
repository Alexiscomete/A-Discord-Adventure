package io.github.alexiscomete.lapinousecond.view.exceptions

import io.github.alexiscomete.lapinousecond.view.ViewType
import io.github.alexiscomete.lapinousecond.view.discord.commands.classes.ACCOUNT_START_COMMAND

class AccountException() : ExceptionWithViewType() {
    override fun getMessageAdaptedForViewType(viewType: ViewType): String {
        return message + "\n" + (
                when (viewType) {
                    ViewType.DISCORD -> "**Pour créer un compte**, tapez la commande $ACCOUNT_START_COMMAND"
                }
                )
    }

    override val message: String
        get() = "Votre compte n'existe pas encore ... créez un compte avant de jouer ! Code erreur : `E0`."
}
