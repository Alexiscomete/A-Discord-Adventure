package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.UserPerms;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.roles.Role;
import io.github.alexiscomete.lapinousecond.roles.RolesEnum;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class RoleCommand extends CommandBot {

    public RoleCommand() {
        super("Voir et gérer les rôles du bot", "role", "-role -> voir ses rôles; -role add [role] [user] -> ajouter un rôle à une personne");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        Player p = saveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            messageCreateEvent.getMessage().reply("... vous n'avez pas de compte");
            return;
        }
        if (messageCreateEvent.isServerMessage()) {
            System.out.println(args.length);

            if (args.length == 1) {
                StringBuilder roles = new StringBuilder();
                for (Role role : p.getRoles()) {
                    roles.append(role.getName()).append(" (").append(role.getServerID()).append(") ");
                    if (role.getServerID() == messageCreateEvent.getServer().get().getId()) {
                        roles.append("-> ici");
                    }
                    roles.append("\n");
                }
                messageCreateEvent.getMessage().reply(new EmbedBuilder().setDescription(roles.toString()).setColor(Color.BLUE).setTitle("Roles"));

            } else if (args[1].equalsIgnoreCase("add")) {
                if (args.length > 3) {
                    if (UserPerms.check(p.getId(), new String[]{"PLAY", "MANAGE_ROLES"})) {
                        addRole(args, messageCreateEvent);
                    } else if ((args[2].equals("SERVER_ADMIN") || args[2].equals("PROJECT_ADMIN")) && RolesEnum.check(new String[]{"SERVER_OWNER"}, p.getRoles(), messageCreateEvent.getServer().get().getId())) {
                        addRole(args, messageCreateEvent);
                    } else if (!args[2].equals("SERVER_OWNER") && !args[2].equals("SERVER_ADMIN") && !args[2].equals("PROJECT_ADMIN") && (RolesEnum.check(new String[]{"SERVER_OWNER"}, p.getRoles(), messageCreateEvent.getServer().get().getId())) || RolesEnum.check(new String[]{"SERVER_ADMIN"}, p.getRoles(), messageCreateEvent.getServer().get().getId()) || RolesEnum.check(new String[]{"PROJECT_ADMIN"}, p.getRoles(), messageCreateEvent.getServer().get().getId())) {
                        addRole(args, messageCreateEvent);
                    } else {
                        messageCreateEvent.getMessage().reply("Vous n'avez pas les permissions pour donner ce rôle");
                    }
                } else {
                    messageCreateEvent.getMessage().reply("Il manque des arguments -help role");
                }


            } else {
                messageCreateEvent.getMessage().reply("-help role");
            }
        } else {
            messageCreateEvent.getMessage().reply("Command non disponible en MP");
        }
    }

    private void addRole(String[] args, MessageCreateEvent messageCreateEvent) {
        try {
            RolesEnum rolesEnum = RolesEnum.valueOf(args[2].toUpperCase());
            Role role = rolesEnum.getInstance(messageCreateEvent.getServer().get().getId());
            Player dest = saveManager.getPlayer(Long.parseLong(args[3]));
            if (hasRole(dest, role)) {
                messageCreateEvent.getMessage().reply("Cette personne a déjà ce rôle sur ce serveur");
            } else {
                dest.addRole(role);
                messageCreateEvent.getMessage().reply("Role ajouté ✅");
            }
        } catch (IllegalArgumentException e) {
            messageCreateEvent.getMessage().reply("Argument 2 ou 3 invalide");
        }
    }

    public boolean hasRole(Player p, Role roleI) {
        for (Role role :
                p.getRoles()) {
            if (role.getProgName().equals(roleI.getProgName()) && role.getServerID() == roleI.getServerID()) {
                return true;
            }
        }
        return false;
    }
}
