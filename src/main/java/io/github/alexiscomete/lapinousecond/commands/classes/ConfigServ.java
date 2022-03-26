package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import io.github.alexiscomete.lapinousecond.worlds.World;
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Objects;
import java.util.function.Consumer;

public class ConfigServ extends CommandWithAccount {

    public ConfigServ() {
        super("Configuration du serveur, config info pour la configuration actuelle", "config", "Permet de configurer le serveur actuel si c'est le votre, config info permet sinon de voir les infos du serveur (pour tout le monde)");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        if (messageCreateEvent.isServerMessage() && messageCreateEvent.getServer().isPresent() && messageCreateEvent.getMessageAuthor().asUser().isPresent()) {
            ServerBot server = saveManager.servers.get(messageCreateEvent.getServer().get().getId());
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
                        saveManager.servers.getHashMap().put(server.getId(), server);
                        saveManager.servers.add(messageCreateEvent.getServer().get().getId());
                        messageCreateEvent.getMessage().reply("Commençons par configurer le nom (entrez un nom) :");
                        ServerBot finalServer = server;
                        Long id = messageCreateEvent.getMessageAuthor().getId();
                        TextChannel textChannel = messageCreateEvent.getChannel();
                        Main.getMessagesManager().setValueAndRetry(textChannel, id, "namerp", "Maintenant la description :", 50, finalServer,
                                () -> Main.getMessagesManager().setValueAndRetry(textChannel, id, "descr", "Maintenant le message d' arrivé sur votre serveur : ", 500, finalServer,
                                        () -> {
                                            EmbedBuilder embedBuilder = new EmbedBuilder()
                                                    .setTitle("Les mondes")
                                                    .setDescription("Maintenant configurons le monde de votre serveur ...");
                                            for (WorldEnum worldEnum :
                                                    WorldEnum.values()) {
                                                World w = worldEnum.getWorld();
                                                embedBuilder.addField(w.getName(), "RP : " + w.getNameRP() + "\nNom à entrer : " + w.getProgName() + "\nDescription : " + w.getDesc());
                                            }
                                            Main.getMessagesManager().setValueAndRetry(textChannel, id, "welcome", embedBuilder, 1500, finalServer, () -> {
                                                Main.getMessagesManager().addListener(textChannel, id, new Consumer<>() {
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
                                                            Main.getMessagesManager().addListener(messageCreateEvent.getChannel(), id, this);
                                                        }
                                                    }
                                                });
                                            });
                                        }));
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
                            messageCreateEvent.getMessage().reply("impossible pour le moment");
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
                messageCreateEvent.getMessage().reply("Bien essayé, mais vous ne pouvez pas configurer un serveur qui n'est pas le votre");
            }
        } else {
            messageCreateEvent.getMessage().reply("Comment voulez vous configurer un serveur sans être dans un serveur ?");
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
