package io.github.alexiscomete.lapinousecond.commands;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public abstract class CommandInServer extends CommandBot {

    public CommandInServer(String description, String name, String totalDescription, String... perms) {
        super(description, name, totalDescription, perms);
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        SaveManager saveManager = Main.getSaveManager();
        Player p = saveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            messageCreateEvent.getMessage().reply("👀 Utilisez la commande start pour vous créer un compte !");
        } else {
            Optional<Server> servOp = messageCreateEvent.getServer();
            if (servOp.isPresent()) {
                Server serv = servOp.get();
                if (serv.getId() == p.getServer()) {
                    executeC(messageCreateEvent, content, args, p);
                } else {
                    messageCreateEvent.getMessage().reply("Utilisez cette commande dans un salon du serveur actuel : " + p.getServer());
                }
            } else {
                messageCreateEvent.getMessage().reply("Utilisez cette commande dans un salon du serveur actuel : " + p.getServer());
            }
        }
    }

    public abstract void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p);
}
