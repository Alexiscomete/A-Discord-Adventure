package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class PermsManager extends CommandBot {
    public PermsManager() {
        super("Commande permettant de modifier les permissions d'un utilisateur dans le bot", "pm", "pm [user] [perm name] [true/false]; perms : PLAY, CREATE_SERVER, SET_SERVER_SEC, MANAGE_PERMS", "MANAGE_PERMS");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (args.length < 4) {
            messageCreateEvent.getMessage().reply("pm [user] [perm name] [true/false]");
            return;
        }
        boolean value = Boolean.parseBoolean(args[3]);
        String perm = args[2];
        try {
            UserPerms userPerms = SaveManager.getPlayerPerms(Long.parseLong(args[1]));
            if (userPerms.isDefault) {

            }
        } catch (NumberFormatException e) {
            messageCreateEvent.getMessage().reply("Vous devez indiquer un format de nombre valide pour l'argument user");
        }
    }
}
