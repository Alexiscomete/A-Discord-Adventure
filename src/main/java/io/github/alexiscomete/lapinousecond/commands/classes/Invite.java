package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class Invite extends CommandBot {
    public Invite() {
        super("Recevoir l'invitation du bot", "invite", "https://discord.com/oauth2/authorize?client_id=854378559539511346&scope=bot&permissions=8");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setDescription("Avec permission admin : https://discord.com/oauth2/authorize?client_id=854378559539511346&scope=bot&permissions=8")
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setTitle("Invitation du bot")
                .setTimestampToNow();
        messageCreateEvent.getMessage().reply(builder);
    }
}
