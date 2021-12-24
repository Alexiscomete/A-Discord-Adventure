package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class InventoryC extends CommandBot {

    public InventoryC() {
        super("Ouvre l'inventaire", "inv", "Vous permet d'ouvrir votre inventaire et de voir votre avancement dans l'aventure !");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (args.length > 1) {
            try {
                Player p = saveManager.getPlayer(Long.parseLong(args[1]));
                if (p == null) {
                    messageCreateEvent.getMessage().reply("Cette personne n'a pas encore de compte");
                } else {
                    invOf(p, messageCreateEvent);
                }
            } catch (NumberFormatException e) {
                messageCreateEvent.getMessage().reply("Pour voir l'inventiare d'une personne, vous devez indiquez son id");
            }
        } else {
            Player p = saveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
            if (p == null) {
                messageCreateEvent.getMessage().reply("Vous devez d'abord faire la commande start avant de continuer");
            } else {
                invOf(p, messageCreateEvent);
                if (p.getTuto() == 1) {
                    messageCreateEvent.getMessage().reply("Bon ... comme vous l'avez vu vous n'avez pas d'argent (sauf si vous refaites le tuto 👀). Utilisez la commande `work` pour en gagner un peu ...");
                    p.setTuto((short) 3);
                }
            }
        }
    }

    public void invOf(Player p, MessageCreateEvent messageCreateEvent) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("Serveur actuel : " + p.getServer()).setTitle("Inventaire").setColor(Color.green);
        builder.addField("Ressources et argent", "**Rabbitcoins :** " + p.getBal());
        messageCreateEvent.getMessage().reply(builder);
    }

}
