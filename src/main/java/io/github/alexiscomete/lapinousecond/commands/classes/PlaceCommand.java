package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import org.javacord.api.event.message.MessageCreateEvent;

public class PlaceCommand extends CommandWithAccount {
    public PlaceCommand(String description, String name, String totalDescription, String... perms) {
        super(description, name, totalDescription, perms);
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {

    }
}
