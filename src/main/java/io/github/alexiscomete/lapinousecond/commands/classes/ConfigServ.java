package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.message_event.MessagesManager;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.function.Consumer;

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
                        server = new ServerBot(messageCreateEvent.getServer().get().getId());
                        saveManager.getServers().put(server.getId(), server);
                        saveManager.addServer(messageCreateEvent.getServer().get().getId());
                        messageCreateEvent.getMessage().reply("Commençons par configurer le nom (entrez un nom) :");
                        ServerBot finalServer = server;
                        User user = messageCreateEvent.getMessageAuthor().asUser().get();
                        Consumer<MessageCreateEvent> cName = new Consumer<MessageCreateEvent>() {
                            @Override
                            public void accept(MessageCreateEvent messageCreateEvent) {
                                if (messageCreateEvent.getMessageContent().length() < 50) {
                                    finalServer.set("name", messageCreateEvent.getMessageContent());
                                    messageCreateEvent.getMessage().reply("Maintenant la description :");
                                    Consumer<MessageCreateEvent> cDesc = new Consumer<MessageCreateEvent>() {
                                        @Override
                                        public void accept(MessageCreateEvent messageCreateEvent) {
                                            if (messageCreateEvent.getMessageContent().length() < 500) {
                                                finalServer.set("descr", messageCreateEvent.getMessageContent());
                                                messageCreateEvent.getMessage().reply("Maintenant le message d' arrivé sur votre serveur :");
                                                Consumer<MessageCreateEvent> cIn = new Consumer<MessageCreateEvent>() {
                                                    @Override
                                                    public void accept(MessageCreateEvent messageCreateEvent) {
                                                        if (messageCreateEvent.getMessageContent().length() < 1500) {
                                                            finalServer.set("welcome", messageCreateEvent.getMessageContent());
                                                            messageCreateEvent.getMessage().reply("Maintenant le message d' arrivé sur votre serveur :");
                                                            messageCreateEvent.getMessage().reply("Configuration terminée !! Enfin ! (et moi j' ai fini de coder ça, maintenant c'est les lieux 😑) Tapez config name ou config desc pour configurer le nom et la description.");
                                                        } else {
                                                            messageCreateEvent.getMessage().reply("Le message d' arrivé doit être de - de 1500 caractères, réessayez, votre taille :" + messageCreateEvent.getMessageContent().length());
                                                            Main.getMessagesManager().addListener(messageCreateEvent.getChannel(), user, this);
                                                        }
                                                    }
                                                };
                                                Main.getMessagesManager().addListener(messageCreateEvent.getChannel(), messageCreateEvent.getMessageAuthor().asUser().get(), cIn);
                                            } else {
                                                messageCreateEvent.getMessage().reply("La description doit être de - de 500 caractères, réessayez, votre taille :" + messageCreateEvent.getMessageContent().length());
                                                Main.getMessagesManager().addListener(messageCreateEvent.getChannel(), user, this);
                                            }
                                        }
                                    };
                                    Main.getMessagesManager().addListener(messageCreateEvent.getChannel(), messageCreateEvent.getMessageAuthor().asUser().get(), cDesc);
                                } else {
                                    messageCreateEvent.getMessage().reply("Le nom doit être de - de 50 caractères, réessayez, votre taille :" + messageCreateEvent.getMessageContent().length());
                                    Main.getMessagesManager().addListener(messageCreateEvent.getChannel(), user, this);
                                }
                            }
                        };
                        Main.getMessagesManager().addListener(messageCreateEvent.getChannel(), messageCreateEvent.getMessageAuthor().asUser().get(), cName);
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
                            server.set("welcome", name.toString());
                            messageCreateEvent.getMessage().reply("Message d' arrivé modifié");
                        } else {
                            messageCreateEvent.getMessage().reply("Utilisez config [what] [value]. Possibilités de what :\n - name, peut être RP\n - desc, description\n - in, message d' arrivé");
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
