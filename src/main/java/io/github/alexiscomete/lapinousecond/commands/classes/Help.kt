package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.ListenerMain
import io.github.alexiscomete.lapinousecond.*
import io.github.alexiscomete.lapinousecond.commands.CommandBot
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.MessageComponentCreateEvent
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.ExecutionException
import java.util.function.Consumer

class Help : CommandBot(
    "Vous affiche l'aide du bot. (help [commande] pour plus d'informations)",
    "help",
    "Vous affiche l'aide du bot. (help [commande] pour plus d'informations)"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        val builder = EmbedBuilder()
        builder.setDescription("Pensez au préfixe !").setTitle("Aide").setColor(Color.blue)
        if (args.size < 2) {
            val messageBuilder = MessageBuilder()
            addCommands(builder, 0)
            val eventAnswer = EventAnswer(builder)
            messageBuilder.addComponents(eventAnswer.components)
            messageBuilder.setEmbed(builder)
            try {
                eventAnswer.register(messageBuilder.send(messageCreateEvent.channel).get().id)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        } else {
            val commandBot = ListenerMain.commands[args[1]]
            if (commandBot == null) {
                builder.addField("👀", "Commande inconnue")
            } else {
                builder.addField(commandBot.name, commandBot.totalDescription)
            }
            messageCreateEvent.message.reply(builder)
        }
    }

    fun addCommands(embedBuilder: EmbedBuilder, min: Int) {
        val commandBots = ListenerMain.commands.values.toTypedArray()
        if (commandBots.size > min) {
            var i = min
            while (i < commandBots.size && i < min + 10) {
                val commandBot = commandBots[i]
                embedBuilder.addField(commandBot.name, commandBot.description)
                i++
            }
        } else {
            embedBuilder.addField("Erreur", "Impossible de trouver la commande n°$min")
        }
    }

    inner class EventAnswer(private val builder: EmbedBuilder) {
        private var level = 0
        fun next(messageComponentCreateEvent: MessageComponentCreateEvent) {
            if (level + 10 < ListenerMain.commands.size) {
                level += 10
                builder.removeAllFields()
                addCommands(builder, level)
                messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater().removeAllEmbeds()
                    .addEmbed(builder).addComponents(
                    components
                ).update()
            }
        }

        fun last(messageComponentCreateEvent: MessageComponentCreateEvent) {
            if (level > 9) {
                level -= 10
                builder.removeAllFields()
                addCommands(builder, level)
                messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater().removeAllEmbeds()
                    .addEmbed(builder).addComponents(
                    components
                ).update()
            }
        }

        val components: ActionRow
            get() = if (level > 0 && level + 10 < ListenerMain.commands.size) {
                ActionRow.of(
                    Button.success("last_page", "Page précédente"),
                    Button.success("next_page", "Page suivante")
                )
            } else if (level > 0) {
                ActionRow.of(
                    Button.success("last_page", "Page précédente")
                )
            } else if (level + 10 < ListenerMain.commands.size) {
                ActionRow.of(
                    Button.success("next_page", "Page suivante")
                )
            } else {
                ActionRow.of()
            }

        fun register(id: Long) {
            val hashMap = HashMap<String, Consumer<MessageComponentCreateEvent>>()
            hashMap["next_page"] =
                Consumer { messageComponentCreateEvent: MessageComponentCreateEvent -> next(messageComponentCreateEvent) }
            hashMap["last_page"] = Consumer { messageComponentCreateEvent: MessageComponentCreateEvent ->
                this.last(messageComponentCreateEvent)
            }
            Main.buttonsManager.addMessage(id, hashMap)
        }
    }
}