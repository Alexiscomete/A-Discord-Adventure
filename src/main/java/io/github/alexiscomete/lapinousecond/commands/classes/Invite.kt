package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.commands.CommandBot
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import io.github.alexiscomete.lapinousecond.commands.*

val invite = load(Invite())

class Invite : CommandBot(
    "Recevoir l'invitation du bot",
    "invite",
    "https://discord.com/oauth2/authorize?client_id=854378559539511346&scope=bot&permissions=8"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        val builder = EmbedBuilder()
            .setColor(Color.BLUE)
            .setDescription("Avec permission admin : https://discord.com/oauth2/authorize?client_id=854378559539511346&scope=bot&permissions=8")
            .setAuthor(messageCreateEvent.messageAuthor)
            .setTitle("Invitation du bot")
            .setTimestampToNow()
        messageCreateEvent.message.reply(builder)
    }
}