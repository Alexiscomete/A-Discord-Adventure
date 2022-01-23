package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import org.javacord.api.event.message.MessageCreateEvent;

public class PlaceCommand extends CommandWithAccount {
    public PlaceCommand() {
        super("Commande des lieux, configuration + description", "place", "Salut ! Je suis une commande. Pour créer un lieu faites place create_new_place, place list pour la list des lieux du serveur");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        if (messageCreateEvent.getMessage().isServerMessage()) {
            ServerBot serverBot = saveManager.getServer(messageCreateEvent.getServer().get().getId());
            if (serverBot == null) {
                messageCreateEvent.getMessage().reply("Utilisez d'abord le -config");
            } else {
                if (args.length > 1) {
                    switch (args[1]) {
                        case "create_new_place":
                            if (messageCreateEvent.getMessage().getContent().endsWith(String.valueOf(p.getId()))) {
                                
                            } else {

                            }
                            break;
                        case "list":
                            messageCreateEvent.getMessage().reply("Impossible pour le moment");
                            break;
                        default:
                            messageCreateEvent.getMessage().reply("Action inconnue");
                    }
                }
            }
        }
    }
}
