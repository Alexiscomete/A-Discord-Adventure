package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
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
                ServerBot server = saveManager.getServer(messageCreateEvent.getServer().get().getId());
                if (server == null) {
                    if (content.endsWith("oui")) {
                        messageCreateEvent.getMessage().reply("Création en cours ....");
                        ArrayList<Long> longs = saveManager.getTravels();
                        server = new ServerBot(messageCreateEvent.getServer().get().getId());
                        saveManager.getServers().put(server.getId(), server);
                        saveManager.addServer(messageCreateEvent.getServer().get().getId());
                        messageCreateEvent.getMessage().reply("Configuration finie, tapez config name ou config desc pour configurer le nom et la description.");
                    } else {
                        messageCreateEvent.getMessage().reply("En continuant (tapez oui à la fin de la commande), vous vous engagez à fournir aux joueurs un serveur respectueux dans lequel ils peuvent s'intégrer ou continuer leur aventure de de bonnes conditions. Vous acceptez aussi que le bot puisse inviter des personne sur votre serveur");
                    }
                } else {
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("name") && args.length > 2) {
                            StringBuilder name = getStr(args);
                            server.set("namerp", name.toString());
                            messageCreateEvent.getMessage().reply("Nom changé");
                        } else if (args[1].equalsIgnoreCase("desc") && args.length > 2) {
                            StringBuilder name = getStr(args);
                            server.set("descr", name.toString());
                            messageCreateEvent.getMessage().reply("Description modifiée");
                        } else if (args[1].equalsIgnoreCase("travel")) {
                            if (server.getTravel().size() < 5) {
                                ArrayList<Long> longs = saveManager.getTravels();
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
                            server.set("train", name.toString());
                            messageCreateEvent.getMessage().reply("Message d' arrivé modifié");
                        } else if (args[1].equalsIgnoreCase("out") && args.length > 2) {
                            StringBuilder name = getStr(args);
                            server.set("traout", name.toString());
                            messageCreateEvent.getMessage().reply("Message de départ modifié");
                        } else {
                            messageCreateEvent.getMessage().reply("Utilisez config [what] [value]. Possibilités de what :\n - name, peut être RP\n - desc, description\n - in, message d' arrivé\n - out, message de départ");
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
