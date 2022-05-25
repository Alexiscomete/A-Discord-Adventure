package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.map.Map;
import io.github.alexiscomete.lapinousecond.worlds.map.Pixel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

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

        if (args.length > 1) {
            // switch arguments
            switch (args[1]) {
                case "dirt":
                    // check if enough arguments
                    if (args.length < 4) {
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
                    // check if the arguments are in the right range
                    if (Integer.parseInt(args[2]) < 0 || Integer.parseInt(args[2]) > Map.MAP_WIDTH) {
                        messageCreateEvent.getMessage().reply("The first argument must be between 0 and " + Map.MAP_WIDTH);
                        return;
                    }
                    if (Integer.parseInt(args[3]) < 0 || Integer.parseInt(args[3]) > Map.MAP_HEIGHT) {
                        messageCreateEvent.getMessage().reply("The second argument must be between 0 and " + Map.MAP_HEIGHT);
                        return;
                    }
                    // check if the pixel is dirt
                    if (Map.isDirt(Integer.parseInt(args[2]), Integer.parseInt(args[3]))) {
                        messageCreateEvent.getMessage().reply("The pixel is dirt");
                    } else {
                        messageCreateEvent.getMessage().reply("The pixel is not dirt");
                    }
                    break;
                case "zoom_p":
                    // check if the player is in the world DIBIMAP
                    String world = p.getString("world");
                    if (!world.equals("DIBIMAP")) {
                        messageCreateEvent.getMessage().reply("Vous n'êtes pas dans le monde DIBIMAP"); // TODO: add dibi message
                        return;
                    }
                    // get the player's position (x, y) in the world DIBIMAP (x/y_DIBIMAP), x and y are strings
                    String x = p.getString("x_DIBIMAP");
                    String y = p.getString("y_DIBIMAP");
                    // convert the strings to int
                    int x_int = Integer.parseInt(x);
                    int y_int = Integer.parseInt(y);
                    // zoom on the player's position and send the map bigger
                    MessageBuilder messageBuilder2 = new MessageBuilder();
                    messageBuilder2.addAttachment(Map.bigger(Map.zoom(x_int, y_int, 30), 10), "map.png");
                    messageBuilder2.send(messageCreateEvent.getChannel());

                    break;
                case "zoom":
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

                    ArrayList<Place> places = Place.getPlacesWithWorld("DIBIMAP");
                    places.removeIf(place -> place.getX().isEmpty() || place.getY().isEmpty() || place.getX().get() < Integer.parseInt(args[2]) - Integer.parseInt(args[4]) * 2 || place.getX().get() > Integer.parseInt(args[2]) + Integer.parseInt(args[4]) * 2 || place.getY().get() < Integer.parseInt(args[3]) - Integer.parseInt(args[4]) || place.getY().get() > Integer.parseInt(args[3]) + Integer.parseInt(args[4]));
                    Map.getMapWithNames(places, Integer.parseInt(args[2]) - Integer.parseInt(args[4]) * 2, Integer.parseInt(args[3]) - Integer.parseInt(args[4]), Integer.parseInt(args[4]) * 4, Integer.parseInt(args[4]) * 2, image);

                    messageBuilder.addAttachment(image, "zoommap.png");

                    messageBuilder.send(messageCreateEvent.getChannel());
                    break;
                case "findpath": // find path between two points : x1 y1 x2 y2
                    // check if enough arguments
                    if (args.length < 6) {
                        sendArgs(messageCreateEvent, p);
                        return;
                    }
                    // check if the arguments are numbers
                    for (int i = 2; i < 6; i++) {
                        if (isNotNumeric(args[i])) {
                            sendNumberEx(messageCreateEvent, p, i);
                            return;
                        }
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
                    if (Integer.parseInt(args[4]) < 0 || Integer.parseInt(args[4]) > Map.MAP_WIDTH) {
                        messageCreateEvent.getMessage().reply("The third argument must be between 0 and " + Map.MAP_WIDTH);
                        return;
                    }
                    if (Integer.parseInt(args[5]) < 0 || Integer.parseInt(args[5]) > Map.MAP_HEIGHT) {
                        messageCreateEvent.getMessage().reply("The fourth argument must be between 0 and " + Map.MAP_HEIGHT);
                        return;
                    }
                    // send the path
                    ArrayList<Pixel> path = Map.findPath(Map.getNode(Integer.parseInt(args[2]), Integer.parseInt(args[3]), new ArrayList<>()), Map.getNode(Integer.parseInt(args[4]), Integer.parseInt(args[5]), new ArrayList<>()), messageCreateEvent.getChannel());
                    messageCreateEvent.getMessage().reply("Path found : " + path.size() + " steps");
                    StringBuilder sb = new StringBuilder();
                    for (Pixel pixel : path) {
                        sb.append(pixel);
                    }
                    messageCreateEvent.getMessage().reply(sb.toString());
                    MessageBuilder messageBuilder3 = new MessageBuilder();
                    messageBuilder3.addAttachment(Map.drawPath(path), "path.png");
                    messageBuilder3.send(messageCreateEvent.getChannel());
                    break;
                default:
                    sendImpossible(messageCreateEvent, p);
                    break;
            }
        }
    }
}
