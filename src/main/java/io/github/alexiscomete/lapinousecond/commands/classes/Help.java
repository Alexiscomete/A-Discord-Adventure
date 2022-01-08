package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.ListenerMain;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Collection;

public class Help extends CommandBot {

    public Help() {
        super("Vous affiche l'aide du bot. (help [commande] pour plus d'informations)", "help", "Vous affiche l'aide du bot. (help [commande] pour plus d'informations)");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        System.out.println("help!!");
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("Pensez au préfix !").setTitle("Aide").setColor(Color.blue);
        if (args.length < 2) {
            addCommands(builder, 0);
        } else {
            CommandBot commandBot = ListenerMain.commands.get(args[1]);
            if (commandBot == null) {
                builder.addField("👀", "Commande inconnue");
            } else {
                builder.addField(commandBot.getName(), commandBot.getTotalDescription());
            }
        }
        messageCreateEvent.getMessage().reply(builder);

    }

    public void addCommands(EmbedBuilder embedBuilder, int min) {
        CommandBot[] commandBots = ListenerMain.commands.values().toArray(new CommandBot[0]);
        if (commandBots.length > min) {
            for (int i = min; i < commandBots.length && i < min + 10; i++) {
                CommandBot commandBot = commandBots[i];
                embedBuilder.addField(commandBot.getName(), commandBot.getDescription());
            }
        } else {
            embedBuilder.setDescription("Pas de commande n°" + min);
        }
    }
}
