package io.github.alexiscomete.lapinousecond.commands.withslash

import io.github.alexiscomete.lapinousecond.api
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandOption
import java.util.EnumSet

open class Command(
    val name: String,
    val description: String,
    val usage: String,
    discordPerms: EnumSet<PermissionType>? = null,
    inDms: Boolean = true,
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