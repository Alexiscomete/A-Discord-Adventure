package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.entity.entities.PLAYERS
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderFactoryUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.DiscordPlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Message
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.SlashCommandInteraction

val SETTINGS_MAIN_MENU_FACTORY =
    MenuBuilderFactoryUI(
        "Paramètres du bot",
        "Gérez vos paramètres ici !"
    )

fun setUiToSettingsUi(ui: PlayerUI) {
    val pui = SETTINGS_MAIN_MENU_FACTORY
        .build(ui)

    if (ui.getPlayer()["notif"] == "d") {
        pui.addButton(
            "Activer notifications",
            "Vos notifications sont désactivées, ce boutton permet de les activer"
        ) {
            ui.getPlayer()["notif"] = "e"
            ui.addMessage(Message("Succès de l'opération"))
            null
        }
    } else {
        pui.addButton(
            "Désactiver notifications",
            "Vos notifications sont activées, ce boutton permet de les désactiver"
        ) {
            ui.getPlayer()["notif"] = "d"
            ui.addMessage(Message("Succès de l'opération"))
            null
        }
    }

    ui.setLongCustomUI(pui)
}

class SettingsCommandBase : Command(
    "settings",
    "Permet de changer les paramètres du bot, et par exemple d'enlever les notifications",
), ExecutableWithArguments {
    override val fullName: String
        get() = "settings"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        // Je vais utiliser les bases de données suivantes :
        PLAYERS

        val context = contextFor(getAccount(slashCommand.user))
        val ui = DiscordPlayerUI(context, slashCommand as Interaction)
        setUiToSettingsUi(ui)
        ui.updateOrSend()
        context.ui(ui)
    }

}
