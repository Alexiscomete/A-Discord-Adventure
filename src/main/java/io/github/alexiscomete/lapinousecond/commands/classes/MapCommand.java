package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.entity.Player;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import io.github.alexiscomete.lapinousecond.worlds.map.Map;

public class MapCommand extends CommandWithAccount {
    public MapCommand() {
        super("description", "map", "totalDescription");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {

        if (args.length == 1) {
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.addAttachment(Map.getMap(), "map.png");
            messageBuilder.send(messageCreateEvent.getChannel());
            return;
        }

        // check if the player is in the world DIBIMAP
        String world = p.getString("current_world");
        if (!world.equals("DIBIMAP")) {
            messageCreateEvent.getMessage().reply("Vous n'êtes pas dans le monde DIBIMAP"); // TODO: add dibi message
            return;
        }


    }
}
