package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.resources.ResourceManager;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InventoryC extends CommandBot {

    public InventoryC() {
        super("Ouvre l'inventaire", "inv", "Vous permet d'ouvrir votre inventaire et de voir votre avancement dans l'aventure ! Utiliser inv top bal pour connaître le classement des joueurs");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (args.length > 1) {
            if (args[1].equals("top")) {
                if (args.length < 3 || args[2].equals("bal")) {
                    ResultSet resultSet = saveManager.executeQuery("SELECT * FROM players ORDER BY bal LIMIT 10", true);
                    ArrayList<Player> players = new ArrayList<>();
                    try {
                        while (resultSet.next()) {
                            players.add(new Player(resultSet.getLong("id"), resultSet.getDouble("bal"), resultSet.getLong("serv"), resultSet.getShort("tuto"), resultSet.getBoolean("is_verify"), resultSet.getBoolean("has_account"), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getString("roles"), resultSet.getString("resources")));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("Classement des joueurs par bal")
                            .setColor(Color.CYAN);
                    String[] top = {""};
                    final int[] ints = {players.size()};
                    for (Player player : players) {
                        Main.api.getUserById(player.getId()).thenAccept(user -> {
                            System.out.println("...");
                            ints[0]--;
                            top[0] = user.getName() + " -> " + player.getBal() + "\n" + top[0];
                            if (ints[0] == 0) {
                                embedBuilder.setDescription(top[0]);
                                messageCreateEvent.getMessage().reply(embedBuilder);
                            }
                        });
                    }
                } else {
                    messageCreateEvent.getMessage().reply("Seul le classement par bal est disponible pour le moment");
                }
            } else {
                if (args[1].startsWith("<@")) {
                    args[1] = args[1].substring(2, args[1].length()-1);
                }
                try {
                    Player p = saveManager.getPlayer(Long.parseLong(args[1]));
                    if (p == null) {
                        messageCreateEvent.getMessage().reply("Cette personne n'a pas encore de compte");
                    } else {
                        invOf(p, messageCreateEvent);
                    }
                } catch (NumberFormatException e) {
                    messageCreateEvent.getMessage().reply("Pour voir l'inventiare d'une personne, vous devez indiquer son id ou la mentionner");
                }
            }
        } else {
            Player p = saveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
            if (p == null) {
                messageCreateEvent.getMessage().reply("Vous devez d'abord faire la commande start avant de continuer");
            } else {
                invOf(p, messageCreateEvent);
                if (p.getTuto() == 1) {
                    messageCreateEvent.getMessage().reply("Bon ... comme vous l'avez vu vous n'avez normalement pas d'argent. Utilisez la commande `work` pour en gagner un peu ...");
                    p.setTuto((short) 3);
                } else if (p.getTuto() == 4) {
                    messageCreateEvent.getMessage().reply("Vous remarquerez quelques changements. Utilisez -shop pour échanger ce que vous avez récupéré");
                    p.setTuto((short) 5);
                }
            }
        }
    }

    public void invOf(Player p, MessageCreateEvent messageCreateEvent) {
        EmbedBuilder builder = new EmbedBuilder()
                .setDescription("Serveur actuel : " + p.getServer())
                .setTitle("Infos joueur")
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setTimestampToNow()
                .addField("Pixel", "Compte sur l'ORU : " + (p.hasAccount() ? "oui" : "non") + "\nVérification : " + (p.isVerify() ? "oui" : "non") + "\nPixel : " + (p.getX() == -1 ? "pixel inconnu" : ("[" + p.getX() + ":" + p.getY() + "]")))
                .setColor(Color.green)
                .setThumbnail("https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png");
        messageCreateEvent.getMessage().reply(builder);
        StringBuilder re = new StringBuilder()
                .append("Nom -> quantité\n");
        for (ResourceManager reM :
                p.getResourceManagers().values()) {
            re.append(reM.getResource().getName()).append(" -> ").append(reM.getQuantity()).append("\n");
        }
        EmbedBuilder builder2 = new EmbedBuilder()
                .setTitle("Inventaire : ressources, items, argent")
                .setColor(Color.ORANGE)
                .addField("Rabbitcoins", String.valueOf(p.getBal()))
                .addField("Ressources", re.toString());
        messageCreateEvent.getMessage().reply(builder2);
    }

}
