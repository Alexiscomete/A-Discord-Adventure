package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.message_event.ListButtons;
import io.github.alexiscomete.lapinousecond.view.AnswerEnum;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Command to build a building, manage buildings, and use them
 */
public class BuildingCommand extends CommandInServer {

    public BuildingCommand() {
        super("Toutes les actions relatives aux bâtiments", "build", "Utilisation :\n- enter [id] pour entrer dans un bâtiment\n- exit pour sortir\n- la commande seule permet de voir la liste des bâtiments ou de voir les informations rapides\n- infos pour voir les infos détaillées");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        String building = p.getString("building");
        Building building1;
        if (building.equals("") || building.equals("exit")) {
            building1 = null;
        } else {
            System.out.println("ll");
            building1 = Buildings.load(building);
        }
        if (args.length > 1) {
            // all sub commands
            switch (args[1]) {
                // entering in a building
                case "enter":
                    if (building1 == null) {
                        if (args.length > 2) {
                            try {
                                long i = Long.parseLong(args[2]);
                                Place place = p.getPlace();
                                String buildingsString = place.getString("buildings");
                                ArrayList<Building> buildings = Buildings.loadBuildings(buildingsString);
                                System.out.println(buildingsString);
                                System.out.println(buildings.size());
                                Building b = null;
                                for (Building bu :
                                        buildings) {
                                    if (bu.getId() == i) {
                                        b = bu;
                                    }
                                }
                                if (b == null) {
                                    sendImpossible(messageCreateEvent, p);
                                    return;
                                } else {
                                    p.set("building", args[2]);
                                    System.out.println(args[2]);
                                    messageCreateEvent.getMessage().reply(p.getAnswer(AnswerEnum.ENTREE_BUILD, true));
                                }
                            } catch (IllegalArgumentException e) {
                                sendNumberEx(messageCreateEvent, p, 2);
                            }
                        } else {
                            sendArgs(messageCreateEvent, p);
                        }
                    } else {
                        sendImpossible(messageCreateEvent, p);
                    }
                    break;
                // exiting from a building and send the list of buildings
                case "exit":
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p);
                    } else {
                        p.set("building", "exit");
                        sendList(messageCreateEvent, p);
                    }
                    break;
                // send information of the building
                case "infos":
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p);
                    } else {
                        building1.completeInfos(p).send(messageCreateEvent.getChannel());
                    }
                    break;
                // send the list of buildings types or build a new building
                case "build":
                    if (building1 == null) {
                        if (args.length > 2) {
                            try {
                                Buildings b = Buildings.valueOf(args[2].toUpperCase());
                                if (b.isBuild() && b.getBuildingAutorisations().isAutorise(p)) {
                                    Place place = p.getPlace();
                                    Building building2 = new Building(b, p, place);
                                    EmbedBuilder embedBuilder = building2.infos(p);
                                    messageCreateEvent.getMessage().reply(embedBuilder);
                                } else {
                                    System.out.println(b.getBuildingAutorisations().isAutorise(p));
                                    System.out.println(b.isBuild());
                                    sendImpossible(messageCreateEvent, p);
                                }
                            } catch (IllegalArgumentException e) {
                                sendImpossible(messageCreateEvent, p);
                            }
                        } else {
                            sendBuildTypeList(messageCreateEvent, p);
                        }
                    } else {
                        sendImpossible(messageCreateEvent, p);
                    }
                    break;
                case "usage":
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p);
                    } else {
                        messageCreateEvent.getMessage().reply(building1.getUsage());
                    }
                    break;
                case "help":
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p);
                    } else {
                        messageCreateEvent.getMessage().reply(building1.getHelp());
                    }
                default:
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p);
                    } else {
                        building1.interpret(args);
                    }
                    break;
            }
        } else {
            if (building1 == null) {
                sendList(messageCreateEvent, p);
            } else {
                messageCreateEvent.getMessage().reply(building1.infos(p));
            }
        }
    }

    public void sendBuildTypeList(MessageCreateEvent messageCreateEvent, Player p) {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        messageBuilder.setEmbed(embedBuilder);
        ArrayList<Buildings> buildings = new ArrayList<>(Arrays.asList(Buildings.values()));
        ListButtons<Buildings> buildingListButtons = new ListButtons<>(embedBuilder, buildings, this::addListTypeBuild);
        buildingListButtons.register();
        messageBuilder.send(messageCreateEvent.getChannel());
    }

    public void addListTypeBuild(EmbedBuilder embedBuilder, int min, int num, ArrayList<Buildings> list) {
        for (int i = min; i < num; i++) {
            Buildings b = list.get(i);
            embedBuilder.addField(b.getName(), "Peut être construit : " + b.isBuild() + "\nPrix : " + b.getBasePrice(), false);
        }
    }

    public void sendList(MessageCreateEvent messageCreateEvent, Player p) {
        Place place = p.getPlace();
        String buildingsString = place.getString("buildings");
        ArrayList<Building> buildings = Buildings.loadBuildings(buildingsString);
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        messageBuilder.setEmbed(embedBuilder);
        ListButtons<Building> buildingListButtons = new ListButtons<>(embedBuilder, buildings, this::addListBuild);
        buildingListButtons.register();
        messageBuilder.send(messageCreateEvent.getChannel());
    }

    public void addListBuild(EmbedBuilder embedBuilder, int min, int num, ArrayList<Building> uArrayList) {
        for (int i = min; i < min + num; i++) {
            Building u = uArrayList.get(i);
            embedBuilder.addField(u.getString("name").equals("") ? "???" : u.getString("name"), u.getId() + " -> (" + u.getString("type") + " : " + u.getString("build_status") + ") " + u.getString("descr"));
        }
    }
}
