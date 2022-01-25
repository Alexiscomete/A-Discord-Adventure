package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Objects;

public class PlaceCommand extends CommandWithAccount {
    public PlaceCommand() {
        super("Commande des lieux, configuration + description", "place", "Salut ! Je suis une commande. Pour créer un lieu faites place create_new_place, place list pour la list des lieux du serveur");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        if (messageCreateEvent.getMessage().isServerMessage()) {
            ServerBot serverBot = saveManager.getServer(messageCreateEvent.getServer().get().getId());
            if (serverBot == null) {
                messageCreateEvent.getMessage().reply("Utilisez d'abord le -config");
            } else {
                if (args.length > 1) {
                    switch (args[1]) {
                        case "create_new_place":
                            if (messageCreateEvent.getMessageAuthor().canManageRolesOnServer()) {
                                if (messageCreateEvent.getMessage().getContent().endsWith(String.valueOf(p.getId() - 42))) {
                                    switch (serverBot.getString("world")) {
                                        case "NORMAL":
                                            createNormalPlace(messageCreateEvent, serverBot, p);
                                            break;
                                        case "DIBIMAP":
                                            createWorldPlace(messageCreateEvent, serverBot, p);
                                            break;
                                        default:
                                            messageCreateEvent.getMessage().reply("Impossible de créer un lieu officiel pour ce monde");
                                    }
                                } else {
                                    messageCreateEvent.getMessage().reply("**En créant un lieu**, vous garantissez que votre serveur est le **serveur officiel** de **ce lieu**. Si ce n' est pas le cas les modérateurs du bot pourront supprimer le lieu et infliger une pénalité pour le **serveur** sur le bot (ou même une **réinitialisation**). Il existe **d' autres façon** de créer un lieu **non officiel**. Tapez **" + (p.getId() - 42) + "** à la fin de la **même commande** pour valider");
                                    messageCreateEvent.getMessage().delete();
                                }
                            } else {
                                messageCreateEvent.getMessage().reply("Vous devez avoir la permission de gérer les rôles pour utiliser cette commande");
                            }
                            break;
                        case "list":
                            messageCreateEvent.getMessage().reply("Impossible pour le moment");
                            break;
                        default:
                            messageCreateEvent.getMessage().reply("Action inconnue");
                            break;
                    }
                }
            }
        }
    }

    public void createNormalPlace(MessageCreateEvent messageCreateEvent, ServerBot serverBot, Player p) {
        if (serverBot.getArray("places").length == 1 && Objects.equals(serverBot.getArray("places")[0], "")) {
            Place place = new Place()
                    .setAndGet("name", serverBot.getString("namerp"))
                    .setAndGet("world", serverBot.getString("world"))
                    .setAndGet("serv", String.valueOf(serverBot.getId()))
                    .setAndGet("type", "server")
                    .setAndGet("train", serverBot.getString("welcome"))
                    .setAndGet("descr", serverBot.getString("descr"));
            messageCreateEvent.getMessage().reply(place.getPlaceEmbed());
            Main.getMessagesManager().setValueAndRetry(messageCreateEvent.getChannel(), p.getId(), "traout", "Message de sortie mit à jour, configuration terminée. Comment voyager vers d' autres lieux dans ce monde ? Dans ce monde les joueurs dans un serveur peuvent payer pour créer une connection (nom RP à trouver) entre 2 lieux", 1500, serverBot, () -> {});
        } else {
            messageCreateEvent.getMessage().reply("Impossible : un serveur du monde normal ne peut avoir qu' un seul lieu");
        }
    }

    public void createWorldPlace(MessageCreateEvent messageCreateEvent, ServerBot serverBot, Player p) {

    }
}
