package io.github.alexiscomete.lapinousecond;

import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.invite.InviteBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Travel extends CommandInServer {

    public Travel() {
        super("Vous permet de voyager vers un serveur", "travel", "travel / travel list / travel [server id]");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        ServerBot currentServer = SaveManager.getServer(p.getServer());
        if (currentServer == null) {
            messageCreateEvent.getMessage().reply("Impossible de voyager, votre serveur actuel est introuvable dan la base de données, il existe 2 solutions : revenir au hub ou configurer le serveur");
            return;
        }
        if (args.length <= 1 || args[1].equals("list")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("Serveur actuel : " + p.getServer() + "; " + currentServer.getName() + ". Pour voyager vers un serveur, indiquez sont id, le prix  est indiqué à côté. Si le serveur n'est pas dasn la liste, alors le prix est égual à la distance au carré.").setTitle("Voyages disponibles").setColor(Color.green);
        } else {
            ServerBot nextServer = SaveManager.getServer(Long.parseLong(args[1]));
            if (nextServer == null) {
                messageCreateEvent.getMessage().reply("Ce serveur n'est pas dans la base de données");
                return;
            }
            Optional<Server> serverOp = Main.api.getServerById(args[1]);
            if (serverOp.isPresent()) {
                Server server = serverOp.get();
                double price = -1;
                long[] travels = currentServer.getTravel();
                for (long travel : travels) {
                    if (travel == nextServer.getId()) {
                        price = Math.sqrt(Math.pow(currentServer.getX() - nextServer.getX(), 2) + Math.pow(currentServer.getY() - nextServer.getY(), 2) + Math.pow(currentServer.getZ() - nextServer.getZ(), 2));
                    }
                }
                if (price == -1) {
                    price = Math.pow(currentServer.getX() - nextServer.getX(), 2) + Math.pow(currentServer.getY() - nextServer.getY(), 2) + Math.pow(currentServer.getZ() - nextServer.getZ(), 2);
                }
                if (p.getSecurity() > nextServer.sec) {
                    messageCreateEvent.getMessage().reply("Ce serveur a une securité inférieur à celle que vous demandez, choisissez un autre serveur ou utilisez la commande -sec");
                    return;
                }
                if (args.length > 2) {
                    List<ServerChannel> channels = server.getChannels();
                    if (channels.size() == 0)  {
                        messageCreateEvent.getMessage().reply("Bon je pense que ce serveur ne vaux pas la peine : il n'y aucun salon !! Je ne peux même pas vous inviter.");
                        server.getOwner().get().sendMessage("Bon ... si il n'y a même pas de salon dans votre serveur je ne peux rien faire. Pas de chance : une personne voulais le rejoindre");
                        return;
                    }
                    if (p.getBal() < price) {
                        messageCreateEvent.getMessage().reply("Impossible !! Vous n'avez pas assez d'argent pour aller dans ce serveur. Il vous en faut encore " + (price - p.getBal()));
                        return;
                    }
                    InviteBuilder inv = new InviteBuilder(channels.get(0));
                    try {
                        messageCreateEvent.getMessageAuthor().asUser().get().sendMessage(inv.create().get().getUrl().toString());
                        p.setServer(nextServer.getId());
                        p.setBal((long) (p.getBal() - price));
                    } catch (InterruptedException | ExecutionException e) {
                        messageCreateEvent.getMessage().reply("Une erreur est survenue lors de la création de l'invitation.");
                    }
                } else {
                    messageCreateEvent.getMessage().reply("Prix pour aller dans ce serveur : " + price + ". Tapez la même commande avec oui à la fin pour confirmer votre choix (ce dernier est irrévocable)");
                }
            } else {
                messageCreateEvent.getMessage().reply("Désolé, l'id su serveur est incorrect, mais il est dans notre base de données, il est possible que le serveur ai été supprimé ou que le bot ai été retiré");
            }
        }
    }
}
