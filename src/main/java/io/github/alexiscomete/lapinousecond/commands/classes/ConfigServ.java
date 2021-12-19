package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.SaveManager;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.Random;

public class ConfigServ extends CommandBot {

    public ConfigServ() {
        super("Configuration de votre serveur", "config", "Permet de configurer le serveur actuel si c'est le votre");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (messageCreateEvent.isServerMessage()) {
            if (messageCreateEvent.getMessageAuthor().isServerAdmin()) {
                ServerBot server = SaveManager.getServer(messageCreateEvent.getServer().get().getId());
                if (server == null) {
                    if (content.endsWith("oui")) {
                        messageCreateEvent.getMessage().reply("Création en cours ....");
                        Random ran = new Random();
                        int x = ran.nextInt(1000), y = ran.nextInt(1000), z = ran.nextInt(100);
                        ArrayList<Long> longs = SaveManager.getTravels();
                        long[] travels = new long[longs.size()];
                        for (int i = 0; i < longs.size(); i++) {
                            travels[i] = longs.get(i);
                        }
                        server = new ServerBot(x, y, z, messageCreateEvent.getServer().get().getId(), "ee", "ee", longs, (short) 1, "", "");
                        SaveManager.servers.put(server.getId(), server);
                        StringBuilder travels2 = new StringBuilder();
                        for (long tra : travels) {
                            travels2.append(tra).append(";");
                            ServerBot serverBot = SaveManager.getServer(tra);
                            ArrayList<Long> l = serverBot.getTravel();
                            l.add(server.getId());
                            serverBot.setTravel(l);
                        }
                        SaveManager.addServer(x, y, z, messageCreateEvent.getServer().get().getId(), "ee", "ee", travels2.toString(), (short) 1);
                        messageCreateEvent.getMessage().reply("Configuration finie, tapez config name ou config desc pour configurer le nom et la description.");
                    } else {
                        messageCreateEvent.getMessage().reply("En continuant (tapez oui à la fin de la commande), vous vous engagez à fournir aux joueurs un serveur respectueux dans lequel ils peuvent s'intégrer ou continuer leur aventure de de bonnes conditions. Vous acceptez aussi que le bot puisse inviter des personne sur votre serveur");
                    }
                } else {
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("name") && args.length > 2) {
                            StringBuilder name = getStr(args);
                            server.setName(name.toString());
                            messageCreateEvent.getMessage().reply("Fait");
                        } else if (args[1].equalsIgnoreCase("desc") && args.length > 2) {
                            StringBuilder name = getStr(args);
                            server.setDescription(name.toString());
                            messageCreateEvent.getMessage().reply("Fait");
                        } else if (args[1].equalsIgnoreCase("travel")) {
                            if (server.getTravel().size() < 5) {
                                ArrayList<Long> longs = SaveManager.getTravels();
                                for (Long l : longs) {
                                    if (l == server.getId()) {
                                        longs.remove(l);
                                        break;
                                    }
                                }
                                server.setTravel(longs);
                                messageCreateEvent.getMessage().reply("fait ...");
                            } else {
                                messageCreateEvent.getMessage().reply("Vous avez déjà atteint le nombre de serveurs autorisés");
                            }
                        } else if (args[1].equalsIgnoreCase("in") && args.length > 2) {
                            StringBuilder name = getStr(args);
                            server.setIn(name.toString());
                            messageCreateEvent.getMessage().reply("Fait");
                        } else if (args[1].equalsIgnoreCase("out") && args.length > 2) {
                            StringBuilder name = getStr(args);
                            server.setOut(name.toString());
                            messageCreateEvent.getMessage().reply("Fait");
                        } else {
                            messageCreateEvent.getMessage().reply("Utilisez config name [name] pour le nom et config desc [description] pour la description. Le nom peut être RP.");
                        }
                    }
                }
            } else {
                System.out.println("Bien essayé, mais vous ne pouvez pas configurer un serveur qui n'est pas le votre");
            }
        } else {
            System.out.println("Comment voulez vous configurer un serveur sans être dans un serveur ?");
        }
    }

    public static StringBuilder getStr(String[] args) {
        StringBuilder name = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            name.append(args[i]);
            name.append(" ");
        }
        return name;
    }
}