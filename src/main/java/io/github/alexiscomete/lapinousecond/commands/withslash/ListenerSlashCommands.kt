package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.listener.interaction.SlashCommandCreateListener

val commands = LinkedHashMap<String, ExecutableWithArguments>()

class ListenerSlashCommands : SlashCommandCreateListener {
    override fun onSlashCommandCreate(event: SlashCommandCreateEvent) {
        val slashCommand = event.slashCommandInteraction
        try {
            val serverOptional = slashCommand.server
            if (serverOptional.isPresent) {
                val s = serverOptional.get()
                val serverTextChannelOp = slashCommand.channel
                if (serverTextChannelOp.isPresent) {
                    val sC = serverTextChannelOp.get()
                    if (s.id == 904736069080186981L && sC.id != 914268153796771950L) {
                        throw IllegalStateException("Dans ce server uniquement, il est interdit d'utiliser cette commande ici")
                    }
                    val name = sC.asServerTextChannel().get().name
                    // je pense que limiter les salons est important, venture permet d'inclure adventure et aventure
                    if (!(name.contains("bot")
                                || name.contains("command")
                                || name.contains("spam")
                                || name.contains("🤖")
                                || name.contains("venture"))
                    ) {
                        throw IllegalStateException("Le nom du salon doit contenir 'bot', 'command', 'spam', '🤖' ou 'venture' pour être autorisé à utiliser une commande")
                    }
                }
            }
            commands[slashCommand.commandName]?.execute(slashCommand)
        } catch (e: Exception) {
            slashCommand.createImmediateResponder()
                .setContent("Une erreur est survenue. Vérifiez le contenu de la commande. Erreur = `${e.localizedMessage}`")
                .setFlags(MessageFlag.EPHEMERAL).respond()
        }
    }
}