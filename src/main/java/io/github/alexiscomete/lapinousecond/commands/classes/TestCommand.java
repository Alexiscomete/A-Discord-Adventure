package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.PlaceForTest;
import io.github.alexiscomete.lapinousecond.worlds.map.Map;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TestCommand extends CommandWithAccount {

    public TestCommand() {
        super("test", "test", "test");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        // check if enough arguments
        if (args.length < 5) {
            sendArgs(messageCreateEvent, p);
            return;
        }
        // check if the arguments are numbers
        if (isNotNumeric(args[2])) {
            sendNumberEx(messageCreateEvent, p, 2);
            return;
        }
        if (isNotNumeric(args[3])) {
            sendNumberEx(messageCreateEvent, p, 3);
            return;
        }
        if (isNotNumeric(args[4])) {
            sendNumberEx(messageCreateEvent, p, 4);
            return;
        }
        // check if the arguments are in the right range
        if (Integer.parseInt(args[2]) < 0 || Integer.parseInt(args[2]) > Map.MAP_WIDTH) {
            messageCreateEvent.getMessage().reply("The first argument must be between 0 and " + Map.MAP_WIDTH);
            return;
        }
        if (Integer.parseInt(args[3]) < 0 || Integer.parseInt(args[3]) > Map.MAP_HEIGHT) {
            messageCreateEvent.getMessage().reply("The second argument must be between 0 and " + Map.MAP_HEIGHT);
            return;
        }
        // check if arg 4 is < 60
        if (Integer.parseInt(args[4]) > 60) {
            messageCreateEvent.getMessage().reply("The fourth argument must be between 0 and 60");
            return;
        }
        // send the zoom on the map
        MessageBuilder messageBuilder = new MessageBuilder();
        messageCreateEvent.getMessage().reply("Création de la carte en cours et ajout des villes proches ...");

        BufferedImage image = Map.bigger(Map.zoom(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])), 10);

        ArrayList<Place> places = PlaceForTest.generateRandomPlaces(1000);
        places.removeIf(place -> place.getX().isEmpty() || place.getY().isEmpty() || place.getX().get() < Integer.parseInt(args[2]) - Integer.parseInt(args[4]) * 2 || place.getX().get() > Integer.parseInt(args[2]) + Integer.parseInt(args[4]) * 2 || place.getY().get() < Integer.parseInt(args[3]) - Integer.parseInt(args[4]) || place.getY().get() > Integer.parseInt(args[3]) + Integer.parseInt(args[4]));
        Map.getMapWithNames(places, Integer.parseInt(args[2]) - Integer.parseInt(args[4]) * 2, Integer.parseInt(args[3]) - Integer.parseInt(args[4]), Integer.parseInt(args[4]) * 4, Integer.parseInt(args[4]) * 2, image);

        messageBuilder.addAttachment(image, "zoommap.png");

        messageBuilder.send(messageCreateEvent.getChannel());
    }
}
