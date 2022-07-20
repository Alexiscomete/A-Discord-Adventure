package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.interaction.SlashCommand

class Command(
    val name: String,
    val description: String,
    val usage: String,
    val discordPerms: List<PermissionType>? = null,
    val inDms: Boolean = true,
    val botPerms: List<String>? = null,
    val subCommands: List<SubCommand>? = null,
    val subCommandsGroups: List<SubCommandGroup>? = null
) {
    init {
        if (discordPerms == null) {
            SlashCommand.with(name, description)
        }
    }
}