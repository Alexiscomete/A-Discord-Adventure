package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.SaveManager;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

public class SetServerSec extends CommandBot {

    public SetServerSec() {
        super("Set Server Security", "sss", "sss [sec]");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (messageCreateEvent.getMessageAuthor().isBotOwner()) {
            if (messageCreateEvent.getServer().isPresent()) {
                ServerBot serverBot = saveManager.getServer(messageCreateEvent.getServer().get().getId());
                if (serverBot == null) {
                    messageCreateEvent.getMessage().reply("Serveur non configuré");
                    return;
                }
                if (args.length > 1) {
                    serverBot.setSec(Short.parseShort(args[1]));
                } else {
                    messageCreateEvent.getMessage().reply("Oublie de l'argument");
                }
            } else {
                messageCreateEvent.getMessage().reply("...");
            }
        } else {
            messageCreateEvent.getMessage().reply("Seul un modérateur du bot peut utiliser cette commande");
        }
    }
}
