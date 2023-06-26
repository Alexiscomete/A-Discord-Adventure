package io.github.alexiscomete.lapinousecond.view.discord.commands

import io.github.alexiscomete.lapinousecond.view.discord.commands.classes.*
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.listener.interaction.SlashCommandCreateListener
import io.github.alexiscomete.lapinousecond.data.check

val commands = LinkedHashMap<String, ExecutableWithArguments>()

fun loadAllS() {
    WorkCommandBase()
    ShopCommandBase()
    HelpCommandBase()
    AccountCommandBase()
    InvCommandBase()
    MarketCommand()
    MapCommand()
    ConfigCommand()
    InteractCommandBase()
    println("Loaded ${commands.size} commands")
}

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
                        throw IllegalStateException("Dans ce serveur uniquement, il est interdit d'utiliser cette commande ici")
                    }
                    val name = sC.asServerTextChannel().get().name
                    // je pense que limiter les salons est important, venture permet d'inclure adventure et aventure
                    if (!(name.contains("bot") || name.contains("command") || name.contains("spam") || name.contains("🤖") || name.contains(
                            "venture"
                        ))
                    ) {
                        throw IllegalStateException("Le nom du salon doit contenir 'bot', 'command', 'spam', '🤖' ou 'venture' pour être autorisé à utiliser une commande")
                    }
                }
            }
            var commandName = slashCommand.commandName
            // on prend les arguments pour trouver les sous commandes
            val arguments = slashCommand.options
            for (arg in arguments) {
                if (arg.isSubcommandOrGroup) {
                    commandName += " " + arg.name
                    val options = arg.options
                    for (option in options) {
                        if (option.isSubcommandOrGroup) {
                            commandName += " " + option.name
                        }
                    }
                }
            }
            val executableWithArguments =
                commands[commandName] ?: throw IllegalStateException("Commande inconnue \"${commandName}\"")
            if (executableWithArguments.botPerms == null) {
                executableWithArguments.execute(slashCommand)
            } else if (executableWithArguments.botPerms!!.isEmpty()) {
                executableWithArguments.execute(slashCommand)
            } else if (check(slashCommand.user.id, executableWithArguments.botPerms!!)) {
                executableWithArguments.execute(slashCommand)
            } else {
                throw IllegalStateException("Vous n'avez pas les permissions pour utiliser cette commande")
            }
        } catch (e: Exception) {
            slashCommand.createImmediateResponder()
                .setContent("Une erreur est survenue. Vérifiez le contenu de la commande. Erreur = `${e.localizedMessage}`")
                .setFlags(MessageFlag.EPHEMERAL).respond()
            e.printStackTrace()
        }
    }
}