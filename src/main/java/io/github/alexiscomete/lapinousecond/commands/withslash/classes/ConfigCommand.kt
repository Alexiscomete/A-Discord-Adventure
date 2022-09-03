package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.servers
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.interaction.SlashCommandInteraction
import java.util.*

//TODO
class ConfigCommand : Command(
    "config",
    "Permet de configurer le serveur discord et les lieux associés à celui-ci",
    "config",
    inDms = false,
    discordPerms = EnumSet.of(PermissionType.MANAGE_CHANNELS)
), ExecutableWithArguments {
    override val fullName: String
        get() = "config"
    override val botPerms: Array<String>
        get() = arrayOf("CREATE_SERVER")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val serverId = slashCommand.server.get().id
        val server = servers[serverId]
        if (server == null) {
            slashCommand.createImmediateResponder()
                .addEmbed(
                    EmbedBuilder()
                        .setTitle("Votre première configuration")
                        .setDescription("Ceci est la première configuration du serveur discord ! Nous allons tout vous expliquer ci-dessous.")
                )
                .respond()
        } else {
            val world = WorldEnum.valueOf(server["world"]).world
        }
    }

}