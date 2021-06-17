package io.github.alexiscomete.lapinousecond;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class InventoryC extends CommandBot {

    public InventoryC() {
        super("Ouvre l'inventaire", "inv", "Vous permet d'ouvrir votre inventaire et de voir votre avancement dans l'aventure !");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        Player p = SaveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            messageCreateEvent.getMessage().reply("Vous devez d'abord faire la commande start avant de continuer");
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("Serveur actuel : " + p.server).setTitle("Inventaire").setColor(Color.green);
            builder.addField("Ressources et argent", "**Rabbitcoins :** "  + p.bal);
            messageCreateEvent.getMessage().reply(builder);
            if (p.tuto == 2) {
                messageCreateEvent.getMessage().reply("Bon ... comme vous l'avez vu vous n'avez pas d'argent (sauf si vous refaites le tuto 👀). Utilisez la commande `work` pour en gagner un peu ...");
                p.tuto = 3;
            }
        }
    }

}
