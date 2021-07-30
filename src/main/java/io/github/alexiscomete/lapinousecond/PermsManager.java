package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class PermsManager extends CommandBot {
    public PermsManager() {
        super("Commande permettant de modifier les permissions d'un utilisateur dans le bot", "pm", "pm [user] [perm name] [true/false]; perms : PLAY, CREATE_SERVER, SET_SERVER_SEC, MANAGE_PERMS", "MANAGE_PERMS");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
