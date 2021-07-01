package io.github.alexiscomete.lapinousecond;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Optional;

public class Travel extends CommandInServer {

    public Travel() {
        super("Vous permet de voyager vers un serveur", "travel", "travel / travel list / travel [server id]");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        //Main.api.getServerById(id);
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
                if (args.length > 2) {

                } else {

                }
            } else {
                messageCreateEvent.getMessage().reply("Désolé, l'id su serveur est incorrect, mais il est dans notre base de données, il est possible que le serveur ai été supprimé ou que le bot ai été retiré");
            }
        }
    }
}
