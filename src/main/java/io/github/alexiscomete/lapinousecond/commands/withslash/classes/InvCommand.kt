package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import org.javacord.api.interaction.SlashCommandInteraction

class InvCommandBase : Command(
    "inv",
    "Permet de voir l'inventaire du joueur de diverses manières",
    "inv [infos/items/resources]",
    subCommands = listOf(
        InvCommandInfos(),
        InvCommandItems(),
        InvCommandResources()
    )
)

class InvCommandInfos : SubCommand(
    "infos",
    "Permet d'afficher vos informations générales"
), ExecutableWithArguments {
    override val fullName: String
        get() = TODO("Not yet implemented")
    override val botPerms: Array<String>?
        get() = TODO("Not yet implemented")

    override fun execute(slashCommand: SlashCommandInteraction) {
        TODO("Not yet implemented")
    }
}

class InvCommandItems : SubCommand(
    "items",
    "Permet d'afficher vos items"
), ExecutableWithArguments {
    override val fullName: String
        get() = TODO("Not yet implemented")
    override val botPerms: Array<String>?
        get() = TODO("Not yet implemented")

    override fun execute(slashCommand: SlashCommandInteraction) {
        TODO("Not yet implemented")
    }
}

class InvCommandResources : SubCommand(
    "resources",
    "Permet d'afficher vos ressources"
), ExecutableWithArguments {
    override val fullName: String
        get() = TODO("Not yet implemented")
    override val botPerms: Array<String>?
        get() = TODO("Not yet implemented")

    override fun execute(slashCommand: SlashCommandInteraction) {
        TODO("Not yet implemented")
    }
}