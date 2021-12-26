package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.WorkEnum;
import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.time.Instant;
import java.util.Random;

public class Work extends CommandInServer {

    public Work() {
        super("Gagnez de l'argent du jeu", "work", "Utilisable régulièrement pour gagner un peut d'argent du jeu, c'est le moyen le plus simple d'en gagner. Plus vous évolurez dans le jeu plus cette commande vous donnera de l'argent. Votre métier peut aussi influencer le gain. Vous pourrez peut-être choisir un type de work. Bon je parle mais en fait tout ça est loin d'être codé.");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        if (System.currentTimeMillis() - p.getWorkTime() > 200000) {
            WorkEnum[] wo = WorkEnum.values();
            int i = new Random().nextInt(wo.length);
            WorkEnum w = wo[i];
            EmbedBuilder builder = new EmbedBuilder();
            String[] strings = w.getAnswer().split(" rc ");
            int r = new Random().nextInt(w.getMax() - w.getMin()) + w.getMin();
            String answer;
            if (strings.length > 1) {
                answer = strings[0] +" "+  r +" "+ strings[1];
            } else {
                answer = strings[0];
            }
            builder.setDescription(answer).setTitle("Work").setColor(Color.red);
            messageCreateEvent.getMessage().reply(builder);
            p.setBal(p.getBal() + r);
            p.updateWorkTime();
            if (p.getTuto() == 3) {
                messageCreateEvent.getMessage().reply("Félicitations, vous pouvez si vous le souhaitez réutiliser la commande inv pour voir l'argent que vous avez gagné (vous n'avez bien sûr pas besoin de faire ce qui est écrit dessus).");
                p.setTuto((short) 4);
            }
        } else {
            messageCreateEvent.getMessage().reply("Cooldown ! Temps entre 2 work : 200s, temps écoulé : " + (System.currentTimeMillis() - p.getWorkTime()) / 1000 + "s. Temps avant le prochain : <t:" + (Instant.now().getEpochSecond() + 200 - (System.currentTimeMillis() - p.getWorkTime()) / 1000) + ":R>");
        }
    }
}
