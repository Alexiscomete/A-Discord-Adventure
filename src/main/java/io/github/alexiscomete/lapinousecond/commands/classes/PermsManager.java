package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import io.github.alexiscomete.lapinousecond.UserPerms;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.HashMap;

public class PermsManager extends CommandBot {
    public PermsManager() {
        super("Commande permettant de modifier les permissions d'un utilisateur dans le bot", "pm", "pm [user] [perm name] [true/false]; perms : PLAY, CREATE_SERVER, SET_SERVER_SEC, MANAGE_PERMS", "MANAGE_PERMS");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        SaveManager saveManager = Main.getSaveManager();
        if (args.length < 4) {
            messageCreateEvent.getMessage().reply("pm [user] [perm name] [true/false]");
            return;
        }
        boolean value = Boolean.parseBoolean(args[3]);
        String perm = args[2];
        try {
            UserPerms userPerms = saveManager.getPlayerPerms(Long.parseLong(args[1]));
            if (userPerms.isDefault) {
                HashMap<String, String> what = new HashMap<>();
                what.put("ID", String.valueOf(messageCreateEvent.getMessageAuthor().getId()));
                what.put("PLAY", SaveManager.toBooleanString(userPerms.PLAY));
                what.put("CREATE_SERVER", SaveManager.toBooleanString(userPerms.CREATE_SERVER));
                what.put("SET_SERVER_SEC", SaveManager.toBooleanString(userPerms.SET_SERVER_SEC));
                what.put("MANAGE_PERMS", SaveManager.toBooleanString(userPerms.MANAGE_PERMS));
                saveManager.insert("perms", what);
            }
            if (perm.equalsIgnoreCase("MANAGE_PERMS") && !messageCreateEvent.getMessageAuthor().isBotOwner()) {
                messageCreateEvent.getMessage().reply("Impossible .... vous devez être l'owner du bot pour modifier cette permission");
                return;
            }
            saveManager.setValue("perms", messageCreateEvent.getMessageAuthor().getId(), perm, SaveManager.toBooleanString(value));
        } catch (NumberFormatException e) {
            messageCreateEvent.getMessage().reply("Vous devez indiquer un format de nombre valide pour l'argument user");
        }
    }
}
