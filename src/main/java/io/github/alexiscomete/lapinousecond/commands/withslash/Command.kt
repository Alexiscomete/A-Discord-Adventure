package io.github.alexiscomete.lapinousecond.commands.withslash

import io.github.alexiscomete.lapinousecond.api
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandOption
import java.util.EnumSet

class Command(
    val name: String,
    val description: String,
    val usage: String,
    discordPerms: EnumSet<PermissionType>? = null,
    inDms: Boolean = true,
    subCommands: List<Sub>? = null,
    val botPerms: List<String>? = null
) {
    init {
        if (discordPerms == null) {
            if (subCommands == null) {
                SlashCommand.with(name, description)
                    .setEnabledInDms(inDms)
                    .createGlobal(api)
                    .join()
            } else {
                val options = ArrayList<SlashCommandOption>()
                for (sub in subCommands) {
                    options.add(sub.getS())
                }
                SlashCommand.with(name, description, options)
                    .setEnabledInDms(inDms)
                    .createGlobal(api)
                    .join()
            }
        } else {
            if (subCommands == null) {
                SlashCommand.with(name, description)
                    .setEnabledInDms(inDms)
                    .setDefaultEnabledForPermissions(discordPerms)
                    .createGlobal(api)
                    .join()
            } else {
                val options = ArrayList<SlashCommandOption>()
                for (sub in subCommands) {
                    options.add(sub.getS())
                }
                SlashCommand.with(name, description, options)
                    .setEnabledInDms(inDms)
                    .setDefaultEnabledForPermissions(discordPerms)
                    .createGlobal(api)
                    .join()
            }
        }
    }
}