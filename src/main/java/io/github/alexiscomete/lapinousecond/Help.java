package io.github.alexiscomete.lapinousecond;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Collection;

public class Help extends CommandBot {

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        System.out.println("help!!");
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("Pensez au préfix !").setTitle("Aide");
        Collection<CommandBot> commandBots = ListenerMain.commands.values();
        for (CommandBot commandBot : commandBots) {
            builder.addField(commandBot.getName(), commandBot.getDescription());
        }
        messageCreateEvent.getMessage().reply(builder);
    }

    public String getDescription() {
        return "Vous affiche l'aide du bot. (!help [commande] pour plus d'informations)";
    }

    public String getName() {
        return "help";
    }

    public String getTotalDescription() {
        return getDescription();
    }
}
