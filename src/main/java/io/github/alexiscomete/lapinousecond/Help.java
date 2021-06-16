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
        if (args.length < 2) {
            Collection<CommandBot> commandBots = ListenerMain.commands.values();
            for (CommandBot commandBot : commandBots) {
                builder.addField(commandBot.getName(), commandBot.getDescription());
            }
        } else {
            CommandBot commandBot = ListenerMain.commands.get(args[1]);
            if (commandBot == null) {
                builder.addField("👀", "Commande inconnue");
            } else {
                builder.addField(commandBot.getName(), commandBot.getTotalDescription());
            }
            messageCreateEvent.getMessage().reply(builder);
        }
        messageCreateEvent.getMessage().reply(builder);

    }

    public String getDescription() {
        return "Vous affiche l'aide du bot. (help [commande] pour plus d'informations)";
    }

    public String getName() {
        return "help";
    }

    public String getTotalDescription() {
        return getDescription();
    }
}
