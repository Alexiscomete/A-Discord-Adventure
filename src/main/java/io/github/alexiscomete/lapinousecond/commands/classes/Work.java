package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.resources.ResourceManager;
import io.github.alexiscomete.lapinousecond.resources.WorkEnum;
import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import io.github.alexiscomete.lapinousecond.roles.Role;
import io.github.alexiscomete.lapinousecond.roles.RolesEnum;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class Work extends CommandInServer {

    public Work() {
        super("Gagnez de l'argent et/ou des ressources", "work", "Utilisable régulièrement pour gagner un peut d'argent ou des ressources, c'est le moyen le plus simple d'en gagner.", "PLAY");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setTitle("Récompenses de work")
                .setDescription("Les rôles représentent votre implication dans le Dibistan (-role), le work est pour tout le monde");


        StringBuilder roles = new StringBuilder();
        roles.append("Nom salaire ou cooldown\n");

        Optional<User> optionalUser = messageCreateEvent.getMessageAuthor().asUser();
        Optional<Server> optionalServer = messageCreateEvent.getServer();

        if (optionalUser.isPresent() && optionalServer.isPresent()) {
            User user = optionalUser.get();
            Server server = optionalServer.get();

            ArrayList<RolesEnum> rolesArrayList = RolesEnum.getRoles(user, server);
            for (RolesEnum role : rolesArrayList) {
                Role find = null;
                for (Role r : p.getRoles()) {
                    if (r.getRole().equals(role)) {
                        find = r;
                        break;
                    }
                }
                if (find != null) {
                    if (find.isReady()) {
                        roles.append(find.getRole().name).append(" : ").append(find.getRole().salary).append("\n");
                        find.setCurrentCooldown(System.currentTimeMillis() / 1000);
                    } else {
                        roles.append(find.getRole().name).append(" : ").append("Cooldown -> <t:").append(find.getCurrentCooldown() + role.coolDownSize).append(":R>\n");
                    }
                }
            }
        }



        embedBuilder.addField("Roles", roles.toString());

        if (System.currentTimeMillis() - p.getWorkTime() > 200000) {

            WorkEnum[] wo = WorkEnum.values();
            Random random = new Random();

            int total = 0;
            for (WorkEnum w :
                    wo) {
                total += w.getCoef();
            }

            WorkEnum woAnswer = wo[0];
            int ran = random.nextInt(total);
            for (WorkEnum w :
                    wo) {
                ran -= w.getCoef();
                if (ran <= 0) {
                    woAnswer = w;
                    break;
                }
            }

            String[] strings = woAnswer.getAnswer().split(" rc ");
            int r = new Random().nextInt(woAnswer.getMax() - woAnswer.getMin()) + woAnswer.getMin();
            String answer;
            if (strings.length > 1) {
                answer = strings[0] + " " + r + " " + strings[1];
            } else {
                answer = strings[0];
            }
            embedBuilder.addField("Work", answer);

            if (woAnswer.getResource() == null) {
                p.setBal(p.getBal() + r);
            } else {
                ResourceManager resourceManager = p.getResourceManagers().get(woAnswer.getResource());
                if (resourceManager == null)  {
                    resourceManager = new ResourceManager(woAnswer.getResource(), r);
                    p.getResourceManagers().put(woAnswer.getResource(), resourceManager);
                } else {
                    resourceManager.setQuantity(resourceManager.getQuantity() + r);
                }
                p.updateResources();
            }

            p.updateWorkTime();
            if (p.getTuto() == 3) {
                messageCreateEvent.getMessage().reply("La récompense peut varier d'un work à un autre. Utilisez inv ...");
                p.setTuto((short) 4);
            }
        } else {
            embedBuilder.addField("Work", "Cooldown ! Temps entre 2 work : 200s, temps écoulé : " + (System.currentTimeMillis() - p.getWorkTime()) / 1000 + "s. Temps avant le prochain : <t:" + (Instant.now().getEpochSecond() + 200 - (System.currentTimeMillis() - p.getWorkTime()) / 1000) + ":R>");
        }

        messageCreateEvent.getMessage().reply(embedBuilder);
    }
}
