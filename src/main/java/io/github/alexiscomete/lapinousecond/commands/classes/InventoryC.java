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
                    messageCreateEvent.getMessage().reply("Bon ... comme vous l'avez vu vous n'avez normalement pas d'argent. Utilisez la commande `work` pour en gagner un peu ...");
                    p.setTuto((short) 3);
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
                .addField("Pixel", "Compte sur l'ORU : " + (p.hasAccount()?"oui":"non") + "\nVérification : " + (p.isVerify()?"oui":"non") + "\nPixel : " + (p.getX()==-1?"pixel inconnu":("[" + p.getX() + ":" + p.getY() + "]")))
                .setColor(Color.green)
                .setThumbnail("https://tenor.com/view/chest-surprise-loot-drop-gif-19498042");
        messageCreateEvent.getMessage().reply(builder);
        EmbedBuilder builder2 = new EmbedBuilder()
                .setTitle("Inventaire : ressources, items, argent")
                .setColor(Color.ORANGE)
                .addField("Rabbitcoins", String.valueOf(p.getBal()));
        messageCreateEvent.getMessage().reply(builder2);
    }

}
