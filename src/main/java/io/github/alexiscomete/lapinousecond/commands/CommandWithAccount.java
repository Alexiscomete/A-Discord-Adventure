package io.github.alexiscomete.lapinousecond.commands;

import io.github.alexiscomete.lapinousecond.entity.Player;
import org.javacord.api.event.message.MessageCreateEvent;

public abstract class CommandWithAccount extends CommandBot {
    public CommandWithAccount(String description, String name, String totalDescription, String... perms) {
        super(description, name, totalDescription, perms);
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        Player p = saveManager.players.get(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            messageCreateEvent.getMessage().reply("Vous devez d'abord vous créer un compte avec -start");
        } else {
            execute(messageCreateEvent, content, args, p);
        }
    }

    public abstract void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p);
}
