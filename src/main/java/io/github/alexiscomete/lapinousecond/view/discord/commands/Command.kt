package io.github.alexiscomete.lapinousecond.view.discord.commands

import io.github.alexiscomete.lapinousecond.api
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandOption
import java.util.EnumSet

open class Command(
    val name: String,
    val description: String,
    val usage: String,
    discordPerms: EnumSet<PermissionType>? = null, // les permissions de discord sont automatiques, differentes de celles du bot
    inDms: Boolean = true, // discord permet d´interdire les dm
    subCommands: List<Sub>? = null,
    arguments: ArrayList<SlashCommandOption> = arrayListOf()
) {
    init {
        if (this is ExecutableWithArguments) {
            commands[fullName] = this
        }
        if (discordPerms == null) {
            if (subCommands == null) {
                SlashCommand.with(name, description)
                    .setEnabledInDms(inDms)
                    .createGlobal(api)
                    .join()
            } else {
                for (sub in subCommands) {
                    if (sub is ExecutableWithArguments) {
                        commands[sub.fullName] = sub
                    }
                    arguments.add(sub.getS())
                }
                SlashCommand.with(name, description, arguments)
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
                for (sub in subCommands) {
                    if (sub is ExecutableWithArguments) {
                        commands[sub.fullName] = sub
                    }
                    arguments.add(sub.getS())
                }
                SlashCommand.with(name, description, arguments)
                    .setEnabledInDms(inDms)
                    .setDefaultEnabledForPermissions(discordPerms)
                    .createGlobal(api)
                    .join()
            }
        }
    }
}
