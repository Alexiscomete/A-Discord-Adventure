package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.UserPerms;
import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import io.github.alexiscomete.lapinousecond.worlds.World;
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.w3c.dom.Text;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfigServ extends CommandWithAccount {

    public ConfigServ() {
        super("Configuration du serveur, config info pour la configuration actuelle", "config", "Permet de configurer le serveur actuel si c'est le votre, config info permet sinon de voir les infos du serveur (pour tout le monde)");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        if (messageCreateEvent.isServerMessage()) {
            ServerBot server = saveManager.getServer(messageCreateEvent.getServer().get().getId());
            if (messageCreateEvent.getMessage().getContent().equalsIgnoreCase("-config info")) {
                if (server == null) {
                    messageCreateEvent.getMessage().reply("Le serveur n' est pas configuré");
                } else {
                    String name = server.getString("namerp");
                    String descr = server.getString("descr");
                    long id = server.getId();
                    String welcome = server.getString("welcome");
                    String world = server.getString("world");
                    try {
                        World w = WorldEnum.valueOf(world).getWorld();
                        world = w.getName();
                    } catch (IllegalArgumentException e) {
                        world = "Monde invalide";
                    }
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setDescription(!Objects.equals(descr, "") ? descr : "Description invalide")
                            .setTitle(Objects.equals(name, "") ? "Nom invalide" : name)
                            .setAuthor(String.valueOf(id))
                            .addField("Message de bienvenue", Objects.equals(welcome, "") ? "Message d' arrivé invalide" : welcome)
                            .addField("Monde", world);
                    messageCreateEvent.getMessage().reply(embedBuilder);
                }
            } else if (messageCreateEvent.getMessageAuthor().isServerAdmin()) {
                if (server == null) {
                    if (content.endsWith("oui")) {
                        messageCreateEvent.getMessage().reply("Création en cours ....");
                        server = new ServerBot(messageCreateEvent.getServer().get().getId());
                        saveManager.getServers().put(server.getId(), server);
                        saveManager.addServer(messageCreateEvent.getServer().get().getId());
                        messageCreateEvent.getMessage().reply("Commençons par configurer le nom (entrez un nom) :");
                        ServerBot finalServer = server;
                        User user = messageCreateEvent.getMessageAuthor().asUser().get();
                        TextChannel textChannel = messageCreateEvent.getChannel();
                        setValueAndRetry(textChannel, user, "name", "Maintenant la description :", 50, finalServer, () -> {
                            setValueAndRetry(textChannel, user, "descr", "Maintenant le message d' arrivé sur votre serveur : ", 500, finalServer, () -> {

                            });
                        });
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
                                                            EmbedBuilder embedBuilder = new EmbedBuilder()
                                                                    .setTitle("Les mondes")
                                                                    .setDescription("Maintenant configurons le monde de votre serveur ...");
                                                            for (WorldEnum worldEnum :
                                                                    WorldEnum.values()) {
                                                                World w = worldEnum.getWorld();
                                                                embedBuilder.addField(w.getName(), "RP : " + w.getNameRP() + "\nNom à entrer : " + w.getProgName() + "\nDescription : " + w.getDesc());
                                                            }
                                                            messageCreateEvent.getMessage().reply(embedBuilder);

                                                            Consumer<MessageCreateEvent> cW = new Consumer<MessageCreateEvent>() {
                                                                @Override
                                                                public void accept(MessageCreateEvent messageCreateEvent) {
                                                                    try {
                                                                        World world = WorldEnum.valueOf(messageCreateEvent.getMessageContent()).getWorld();
                                                                        finalServer.set("world", world.getProgName());
                                                                        messageCreateEvent.getMessage().reply("Configuration terminée !! Enfin ! (et moi j' ai fini de coder ça, maintenant c'est les lieux 😑) Vous pouvez modifier tout cela à n' importe quel moment avec config [what] [value] et voir la configuration avec -config info.");
                                                                    } catch (IllegalArgumentException e) {
                                                                        messageCreateEvent.getMessage().reply("Ceci n' est pas un monde valide");
                                                                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                                                                .setTitle("Les mondes")
                                                                                .setDescription("SVP lisez");
                                                                        for (WorldEnum worldEnum :
                                                                                WorldEnum.values()) {
                                                                            World w = worldEnum.getWorld();
                                                                            embedBuilder.addField(w.getName(), "RP : " + w.getNameRP() + "\nNom à entrer : " + w.getProgName() + "\nDescription : " + w.getDesc());
                                                                        }
                                                                        messageCreateEvent.getMessage().reply(embedBuilder);
                                                                        Main.getMessagesManager().addListener(messageCreateEvent.getChannel(), user, this);
                                                                    }
                                                                }
                                                            };
                                                            Main.getMessagesManager().addListener(messageCreateEvent.getChannel(), messageCreateEvent.getMessageAuthor().asUser().get(), cW);
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
                            messageCreateEvent.getMessage().reply("Nom modifié");
                        } else if (args[1].equalsIgnoreCase("desc") && args.length > 2) {
                            StringBuilder name = getStr(args);
                            server.set("descr", name.toString());
                            messageCreateEvent.getMessage().reply("Description modifiée");
                        } else if (args[1].equalsIgnoreCase("travel")) {
                            /*if (server.getTravel().size() < 5) {
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
                            }*/
                        } else if (args[1].equalsIgnoreCase("in") && args.length > 2) {
                            setValue(messageCreateEvent, "welcome", "Message d' arrivé modifiée", 1500, server, args);
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

    public void setValueAndRetry(TextChannel textChannel, User user, String prog_name, String message, int len, ServerBot serverBot, Runnable ex) {
        Main.getMessagesManager().addListener(textChannel, user, new Consumer<MessageCreateEvent>() {
            @Override
            public void accept(MessageCreateEvent msgE) {
                if (msgE.getMessageContent().length() <= len) {
                    serverBot.set(prog_name, msgE.getMessageContent());
                    msgE.getMessage().reply(message);
                    ex.run();
                } else {
                    textChannel.sendMessage("Taille maximale : " + len + ". Votre taille : " + msgE.getMessageContent().length());
                    Main.getMessagesManager().addListener(textChannel, user, this);
                }
            }
        });
    }

    public void setValue(MessageCreateEvent messageCreateEvent, String prog_name, String message, int len, ServerBot serverBot, String[] args) {
        StringBuilder value = getStr(args);
        String v = serverBot.testValueAndSet(len, value.toString(), prog_name);
        if (Objects.equals(v, "")) {
            messageCreateEvent.getMessage().reply(message);
        } else {
            messageCreateEvent.getMessage().reply(v);
        }
    }
}
