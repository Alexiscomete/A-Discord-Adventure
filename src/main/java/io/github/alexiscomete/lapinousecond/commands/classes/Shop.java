package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.resources.Resource;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class Shop extends CommandBot {

    final double buyCoef = 1.1, sellCoef = 0.9;

    public Shop() {
        super("Acheter / vendre un item", "shop", "Vous permet d'acheter ou de vendre des items / ressources classiques :\n- 'list' pour voir la liste des items\n- 'buy [name] <quantité>' pour acheter et 'sell [name] <quantité>' pour vendre\n- 'info [name]' pour avoir les informations sur un item\n");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (args.length < 2 || args[1].equals("list")) {
            StringBuilder stringBuilder = new StringBuilder()
                    .append("Resource -> prix d'achat; prix de vente; prix réel");
            for (Resource r :
                    Resource.values()) {
                stringBuilder.append(r.getName()).append(" -> ").append(r.getPrice() * buyCoef).append("; ").append(r.getPrice() * sellCoef).append("; ").append(r.getPrice());
            }
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Liste du shop")
                    .setFooter("Ceci est temporaire en attendant que les marchés qui pourront être contruits dans les serveurs ou les villes");
            messageCreateEvent.getMessage().reply(embedBuilder);
        } else {
            messageCreateEvent.getMessage().reply("Pas encore disponible pour le moment");
        }
    }
}
