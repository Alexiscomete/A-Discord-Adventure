package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import io.github.alexiscomete.lapinousecond.resources.Resource;
import io.github.alexiscomete.lapinousecond.resources.ResourceManager;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class Shop extends CommandInServer {

    final double buyCoef = 1.1, sellCoef = 0.9;

    public Shop() {
        super("Acheter / vendre un item", "shop", "Vous permet d'acheter ou de vendre des items / ressources classiques :\n- 'list' pour voir la liste des items\n- 'buy [name] <quantité>' pour acheter et 'sell [name] <quantité>' pour vendre\n- 'info [name]' pour avoir les informations sur un item\n");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        if (args.length < 2 || args[1].equals("list")) {
            StringBuilder stringBuilder = new StringBuilder()
                    .append("Resource -> prix d'achat; prix de vente; prix réel; nom à entrer");
            for (Resource r :
                    Resource.values()) {
                stringBuilder.append("\n").append(r.getName()).append(" -> ").append(r.getPrice() * buyCoef).append("; ").append(r.getPrice() * sellCoef).append("; ").append(r.getPrice()).append("; ").append(r.getProgName());
            }
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Liste du shop")
                    .setFooter("Ceci est temporaire en attendant que les marchés qui pourront être contruits dans les serveurs ou les villes")
                    .addField("Resources", stringBuilder.toString());
            messageCreateEvent.getMessage().reply(embedBuilder);
            if (p.getTuto() == 5) {
                messageCreateEvent.getMessage().reply("Je vous laisse découvrir la suite tout seul ...");
                p.setTuto((short) 6);
            }
        } else if (args.length > 2) {
            if (args[1].equals("buy")) {
                try {
                    Resource resource = Resource.valueOf(args[2].toUpperCase());
                    int quantity = 1;
                    double price = 0;
                    if (args.length > 3) {
                        quantity = Integer.parseInt(args[3]);
                        if (quantity <= 0) {
                            messageCreateEvent.getMessage().reply("Evitez ce genre de transactions ... petite pénalité");
                            price = resource.getPrice();
                        }
                    }
                    price += quantity * resource.getPrice() * buyCoef;
                    if (p.getBal() >= price) {
                        p.setBal(p.getBal() - price);
                        ResourceManager resourceManager = p.getResourceManagers().get(resource);
                        if (resourceManager == null) {
                            resourceManager = new ResourceManager(resource, quantity);
                            p.getResourceManagers().put((resource), resourceManager);
                        } else {
                            resourceManager.setQuantity(resourceManager.getQuantity() + quantity);
                        }
                        p.updateResources();
                        messageCreateEvent.getMessage().reply("Transaction effectuée");
                    } else {
                        messageCreateEvent.getMessage().reply("La transaction de " + price + " rc pour " + quantity + " " + resource.getName() + " n'a pas pu être effectuée car vous n'aviez pas assez de rc");
                    }
                } catch (IllegalArgumentException e) {
                    messageCreateEvent.getMessage().reply("Cette resource n'existe pas ou la quantité entrée est invalide");
                }
            } else if (args[1].equals("sell")) {
                try {
                    Resource resource = Resource.valueOf(args[2].toUpperCase());
                    int quantity = 1;
                    double price = 0;
                    if (args.length > 3) {
                        quantity = Integer.parseInt(args[3]);
                        if (quantity <= 0) {
                            messageCreateEvent.getMessage().reply("Evitez ce genre de transactions ... petite pénalité");
                            quantity++;
                        }
                    }
                    price += quantity * resource.getPrice() * sellCoef;
                    ResourceManager resourceManager = p.getResourceManagers().get(resource);
                    if (resourceManager == null) {
                        resourceManager = new ResourceManager(resource, 0);
                        p.getResourceManagers().put((resource), resourceManager);
                        messageCreateEvent.getMessage().reply("Transaction impossible car vous n'avez pas les ressources demandées");
                        p.updateResources();
                    } else {
                        if (resourceManager.getQuantity() >= quantity) {
                            p.setBal(p.getBal() + price);
                            resourceManager.setQuantity(resourceManager.getQuantity() - quantity);
                            p.updateResources();
                            messageCreateEvent.getMessage().reply("Transaction effectuée");
                        } else {
                            messageCreateEvent.getMessage().reply("La transaction de " + quantity + " " + resource.getName() + " pour " + price + " rc n'a pas pu être effectuée car vous n'aviez pas assez de " + resource.getName());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    messageCreateEvent.getMessage().reply("Cette resource n'existe pas ou la quantité entrée est invalide");
                }
            } else {
                messageCreateEvent.getMessage().reply("Pas encore disponible pour le moment");
            }
        } else {
            messageCreateEvent.getMessage().reply("Pas encore disponible pour le moment");
        }
    }
}
