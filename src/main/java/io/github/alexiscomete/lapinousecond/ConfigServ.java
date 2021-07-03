package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Random;

public class ConfigServ extends CommandBot {

    public ConfigServ() {
        super("Configuration de votre serveur", "config", "Permet de configurer le serveur actuel si c'est le votre");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (messageCreateEvent.isServerMessage()) {
            if (messageCreateEvent.getMessageAuthor().isServerAdmin()) {
                ServerBot server = SaveManager.getServer(messageCreateEvent.getServer().get().getId());
                if (server == null) {
                    messageCreateEvent.getMessage().reply("Création en cours ....");
                    Random ran = new Random();
                    int x = ran.nextInt(100000), y = ran.nextInt(100000), z = ran.nextInt(10000);

                    long[] travels = {};
                    server = new ServerBot(x, y, z, messageCreateEvent.getServer().get().getId(), "", "", travels, (short) 1);
                    SaveManager.servers.put(server.getId(), server);

                } else {
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("name") && args.length > 2) {
                            StringBuilder name = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                name.append(args[i]);
                            }
                            server.setName(name.toString());
                            messageCreateEvent.getMessage().reply("Fait");
                        } else if (args[1].equalsIgnoreCase("desc") && args.length > 2) {
                            StringBuilder name = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                name.append(args[i]);
                            }
                            server.setDescription(name.toString());
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
}
