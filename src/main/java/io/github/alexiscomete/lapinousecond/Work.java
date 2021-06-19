package io.github.alexiscomete.lapinousecond;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Optional;
import java.util.Random;

public class Work extends CommandBot {

    public Work() {
        super("Gagnez de l'argent du jeu", "work", "Utilisable régulièrement pour gagner un peut d'argent du jeu, c'est le moyen le plus simple d'en gagner. Plus vous évolurez dans le jeu plus cette commande vous donnera de l'argent. Votre métier peut aussi influencer le gain. Vous pourrez peut-être choisir un type de work. Bon je parle mais en fait tout ça est loin d'être codé.");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        Player p = SaveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            messageCreateEvent.getMessage().reply("👀 Utilisez la commande start pour vous créer un compte !");
        } else {
            Optional<Server> servOp = messageCreateEvent.getServer();
            if (servOp.isPresent()) {
                Server serv = servOp.get();
                if (serv.getId() == p.server) {
                    if (System.currentTimeMillis() - p.getWorkTime() > 200000) {
                        WorkEnum[] wo = WorkEnum.values();
                        int i = new Random().nextInt(wo.length);
                        WorkEnum w = wo[i];
                        EmbedBuilder builder = new EmbedBuilder();
                        String[] strings = w.answer.split("rc");
                        int r = new Random().nextInt(w.max - w.min) + w.min;
                        String answer;
                        if (strings.length > 1) {
                            answer = strings[0] + r + strings[1];
                        } else {
                            answer = strings[0];
                        }
                        builder.setDescription(answer).setTitle("Work").setColor(Color.red);
                        messageCreateEvent.getMessage().reply(builder);
                        p.setBal(p.bal + r);
                        p.setWorkTime(System.currentTimeMillis());
                        if (p.tuto == 3) {
                            messageCreateEvent.getMessage().reply("Félicitations, vous pouvez si vous le souhaitez réutiliser la commande inv pour voir l'argent que vous avez gagné (vous n'avez bien sûr pas besoin de faire ce qui est écrit dessus).");
                            p.setTuto((short) 4);
                        }
                    } else {
                        messageCreateEvent.getMessage().reply("Cooldown ! Temps entre 2 work : 200s, temps écoulé : " + (System.currentTimeMillis() - p.getWorkTime()) / 1000 + "s");
                    }
                } else {
                    messageCreateEvent.getMessage().reply("Utilisez cette commande dans un salon du serveur actuel : " + p.server);
                }
            } else {
                messageCreateEvent.getMessage().reply("Utilisez cette commande dans un salon du serveur actuel : " + p.server);
            }
        }
    }
}
