package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.WorkEnum;
import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import io.github.alexiscomete.lapinousecond.roles.Role;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.time.Instant;
import java.util.Random;

public class Work extends CommandInServer {

    public Work() {
        super("Gagnez de l'argent du jeu", "work", "Utilisable régulièrement pour gagner un peut d'argent du jeu, c'est le moyen le plus simple d'en gagner. Plus vous évolurez dans le jeu plus cette commande vous donnera de l'argent. Votre métier peut aussi influencer le gain. Vous pourrez peut-être choisir un type de work. Bon je parle mais en fait tout ça est loin d'être codé.", "PLAY");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setTitle("Récompenses de work")
                .setDescription("Les rôles représentent votre implication dans la RPBD (-role), le work est pour tout le monde");


        StringBuilder roles = new StringBuilder();
        for (Role role : p.getRoles()) {
            roles.append(role.getName()).append(" (").append(role.getServerID()).append(") ");
            if (role.getCoolDown() + role.getCoolDownSize() > System.currentTimeMillis()) {
                roles.append(role.getSalary());
                p.setBal(p.getBal() + role.getSalary());
                role.updateCoolDown();
            } else {
                roles.append("<t:").append((Instant.now().getEpochSecond() + role.getCoolDownSize()/1000 - (System.currentTimeMillis() - role.getCoolDown()) / 1000)).append(":R>");
            }
            roles.append("\n");
        }
        embedBuilder.addField("Roles", roles.toString());


        if (System.currentTimeMillis() - p.getWorkTime() > 200000) {
            WorkEnum[] wo = WorkEnum.values();
            int i = new Random().nextInt(wo.length);
            WorkEnum w = wo[i];
            String[] strings = w.getAnswer().split(" rc ");
            int r = new Random().nextInt(w.getMax() - w.getMin()) + w.getMin();
            String answer;
            if (strings.length > 1) {
                answer = strings[0] + " " + r + " " + strings[1];
            } else {
                answer = strings[0];
            }
            embedBuilder.addField("Work", answer);
            p.setBal(p.getBal() + r);
            p.updateWorkTime();
            if (p.getTuto() == 3) {
                messageCreateEvent.getMessage().reply("Félicitations, vous pouvez si vous le souhaitez réutiliser la commande inv pour voir l'argent que vous avez gagné ||(vous n'avez bien sûr pas besoin de faire ce qui est écrit dessus)||.");
                p.setTuto((short) 4);
            }
        } else {
            embedBuilder.addField("Work", "Cooldown ! Temps entre 2 work : 200s, temps écoulé : " + (System.currentTimeMillis() - p.getWorkTime()) / 1000 + "s. Temps avant le prochain : <t:" + (Instant.now().getEpochSecond() + 200 - (System.currentTimeMillis() - p.getWorkTime()) / 1000) + ":R>");
        }
        messageCreateEvent.getMessage().reply(embedBuilder);
    }
}
