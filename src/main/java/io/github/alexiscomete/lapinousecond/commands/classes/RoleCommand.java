package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import io.github.alexiscomete.lapinousecond.roles.Role;
import org.javacord.api.event.message.MessageCreateEvent;

import java.time.Instant;

public class RoleCommand extends CommandInServer {

    public RoleCommand() {
        super("Voir et gérer les rôles du bot", "role", "-role -> voir ses rôles; -role add [role] [user] -> ajouter un rôle à une personne");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {

        if (args.length == 1) {
            StringBuilder roles = new StringBuilder();
            for (Role role : p.getRoles()) {
                roles.append(role.getName()).append(" (").append(role.getServerID()).append(") ");
                if (role.getServerID() == messageCreateEvent.getServer().get().getId()) {
                    roles.append("-> ici");
                }
                roles.append("\n");
            }


        } else if (args[1].equalsIgnoreCase("add")) {
            if (args.length > 3) {

            } else {

            }
        } else {
            messageCreateEvent.getMessage().reply("-help role");
        }
    }
}
