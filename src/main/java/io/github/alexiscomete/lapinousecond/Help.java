package io.github.alexiscomete.lapinousecond;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Collection;

public class Help extends CommandBot {

    public Help() {
        super("Vous affiche l'aide du bot. (help [commande] pour plus d'informations)", "help", "Vous affiche l'aide du bot. (help [commande] pour plus d'informations)");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        System.out.println("help!!");
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("Pensez au préfix !").setTitle("Aide").setColor(Color.blue);
        if (args.length < 2) {
            Collection<CommandBot> commandBots = ListenerMain.commands.values();
            for (CommandBot commandBot : commandBots) {
                builder.addField(commandBot.name, commandBot.description);
            }
        } else {
            CommandBot commandBot = ListenerMain.commands.get(args[1]);
            if (commandBot == null) {
                builder.addField("👀", "Commande inconnue");
            } else {
                builder.addField(commandBot.name, commandBot.totalDescription);
            }
        }
        messageCreateEvent.getMessage().reply(builder);

    }
}
