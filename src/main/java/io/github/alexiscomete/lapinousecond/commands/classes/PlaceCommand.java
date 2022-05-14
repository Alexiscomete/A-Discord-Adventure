package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.PlaceZones;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import io.github.alexiscomete.lapinousecond.worlds.Zone;
import io.github.alexiscomete.lapinousecond.worlds.map.Map;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SelectMenuInteraction;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class PlaceCommand extends CommandWithAccount {
    public PlaceCommand() {
        super("Commande des lieux, configuration + description", "place", "Salut ! Je suis une commande. Pour créer un lieu faites place create_new_place, place list pour la list des lieux du serveur");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        if (messageCreateEvent.getMessage().isServerMessage()) {
            ServerBot serverBot = saveManager.servers.get(messageCreateEvent.getServer().get().getId());
            if (serverBot == null) {
                messageCreateEvent.getMessage().reply("Utilisez d'abord le -config");
            } else {
                if (args.length > 1) {
                    switch (args[1]) {
                        case "create_new_place":
                            if (messageCreateEvent.getMessageAuthor().canManageRolesOnServer() && messageCreateEvent.getMessageAuthor().canManageServer() && messageCreateEvent.getMessageAuthor().canCreateChannelsOnServer()) {
                                if (messageCreateEvent.getMessage().getContent().endsWith(String.valueOf(p.getId() - 42))) {
                                    switch (serverBot.getString("world")) {
                                        case "NORMAL":
                                            createNormalPlace(messageCreateEvent, serverBot, p);
                                            break;
                                        case "DIBIMAP":
                                            createWorldPlace(messageCreateEvent, serverBot, p, args);
                                            break;
                                        default:
                                            messageCreateEvent.getMessage().reply("Impossible de créer un lieu officiel pour ce monde");
                                    }
                                } else {
                                    messageCreateEvent.getMessage().reply("**En créant un lieu**, vous garantissez que votre serveur est le **serveur officiel** de **ce lieu**. Si ce n' est pas le cas les modérateurs du bot pourront supprimer le lieu et infliger une pénalité pour le **serveur** sur le bot (ou même une **réinitialisation**). Il existe **d' autres façon** de créer un lieu **non officiel**. Tapez **" + (p.getId() - 42) + "** à la fin de la **même commande** pour valider");
                                    messageCreateEvent.getMessage().delete();
                                }
                            } else {
                                messageCreateEvent.getMessage().reply("Vous devez avoir la permission de gérer les rôles, le serveur et les salons pour utiliser cette commande");
                            }
                            break;
                        case "list":
                            ArrayList<Place> places = Place.getPlacesWithWorld(serverBot.getString("world"));
                            MessageBuilder messageBuilder = new MessageBuilder();
                            EmbedBuilder builder = new EmbedBuilder();
                            setPlaceEmbed(builder, 0, Math.min(places.size(), 11), places);
                            EventAnswer eventAnswer = new EventAnswer(builder, places);
                            messageBuilder.addComponents(eventAnswer.getComponents());
                            messageBuilder.setEmbed(builder);
                            try {
                                eventAnswer.register(messageBuilder.send(messageCreateEvent.getChannel()).get().getId());
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "links":
                            messageCreateEvent.getMessage().reply("Tout les lieux qui ont un lien de voyage avec le lieu de votre serveur");
                            Place place = saveManager.places.get(Long.parseLong(serverBot.getString("places")));
                            ArrayList<Place> places1 = Place.toPlaces(place.getString("connections"));
                            MessageBuilder messageBuilder1 = new MessageBuilder();
                            EmbedBuilder builder1 = new EmbedBuilder();
                            setPlaceEmbed(builder1, 0, Math.min(places1.size(), 11), places1);
                            EventAnswer eventAnswer1 = new EventAnswer(builder1, places1);
                            messageBuilder1.addComponents(eventAnswer1.getComponents());
                            messageBuilder1.setEmbed(builder1);
                            try {
                                eventAnswer1.register(messageBuilder1.send(messageCreateEvent.getChannel()).get().getId());
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "add_link":
                            if (args.length > 2) {
                                try {
                                    Place place1 = saveManager.places.get(Long.parseLong(serverBot.getString("places")));
                                    Place place2 = saveManager.places.get(Long.parseLong(args[2]));
                                    if (Objects.equals(place1.getString("world"), place2.getString("world"))) {
                                        if (place1.getString("world").equals("NORMAL")) {
                                            double bal = p.getBal();
                                            if (bal < 500) {
                                                messageCreateEvent.getMessage().reply("Impossible de créer un lien : vous devez avoir au minimum 500 rb");
                                                return;
                                            }
                                            ArrayList<Place> connections1 = Place.toPlaces(place1.getString("connections"));
                                            ArrayList<Place> connections2 = Place.toPlaces(place2.getString("connections"));
                                            if (connections1.contains(place2)) {
                                                messageCreateEvent.getMessage().reply("Cette connection existe déjà");
                                                return;
                                            }
                                            p.setBal(bal - 500);
                                            connections1.add(place2);
                                            connections2.add(place1);
                                            place1.set("connections", placesToString(connections1));
                                            place2.set("connections", placesToString(connections2));
                                        } else {
                                            messageCreateEvent.getMessage().reply("Impossible pour ce monde pour le moment");
                                        }
                                    } else {
                                        messageCreateEvent.getMessage().reply("Ce lieu n'est pas dans le même monde, donc pas de route entre les 2");
                                    }
                                } catch (NumberFormatException e) {
                                    messageCreateEvent.getMessage().reply("Ceci n'est pas un nombre valide (arg 2)");
                                }
                            } else {
                                messageCreateEvent.getMessage().reply("Action impossible : précisez l'id du lieu pour créer un lien");
                            }
                            break;
                        // zones
                        case "add_zone":
                            Place placeParent = getPlaceParent(serverBot);
                            if (!placeParent.getString("world").equals("DIBIMAP")) {
                                throw new IllegalArgumentException("Ce monde ne prend pas en charge les zones");
                            }
                            if (args.length < 6) {
                                throw new IllegalArgumentException("Il manque des arguments pour créer une zone : x1 y1 x2 y2");
                            }
                            if (isNotNumeric(args[2]) || isNotNumeric(args[3]) || isNotNumeric(args[4]) || isNotNumeric(args[5])) {
                                throw new IllegalArgumentException("Les coordonnées doivent être des nombres");
                            }
                            int x1 = Integer.parseInt(args[2]);
                            int y1 = Integer.parseInt(args[3]);
                            int x2 = Integer.parseInt(args[4]);
                            int y2 = Integer.parseInt(args[5]);
                            if (x1 > x2) {
                                int tmp = x1;
                                x1 = x2;
                                x2 = tmp;
                            }
                            if (y1 > y2) {
                                int tmp = y1;
                                y1 = y2;
                                y2 = tmp;
                            }
                            if (x1 < 0 || y1 < 0) {
                                throw new IllegalArgumentException("Les coordonnées doivent être positives");
                            }
                            if (Map.MAP_HEIGHT < y2 || Map.MAP_WIDTH < x2) {
                                throw new IllegalArgumentException("Les coordonnées sont en dehors de la carte");
                            }

                            PlaceZones placeZones = new PlaceZones(placeParent.getID());

                            Zone zone = new Zone(x1, y1, x2, y2);
                            placeZones.addZone(zone);

                            messageCreateEvent.getMessage().reply("Zone ajoutée");
                            break;
                        case "del_zone":
                            Place placeParentDel = getPlaceParent(serverBot);
                            if (!placeParentDel.getString("world").equals("DIBIMAP")) {
                                throw new IllegalArgumentException("Ce monde ne prend pas en charge les zones");
                            }

                            PlaceZones placeZonesDel = new PlaceZones(placeParentDel.getID());

                            ArrayList<SelectMenuOption> options = new ArrayList<>();
                            int i = 0;
                            for (Zone zoneDel : placeZonesDel.getZones()) {
                                options.add(SelectMenuOption.create(String.valueOf(i), zoneDel.toString()));
                                i++;
                            }
                            long id = SaveLocation.generateUniqueID();
                            ActionRow actionRow = ActionRow.of(SelectMenu.create(String.valueOf(id), "Zone à supprimer", options));
                            new MessageBuilder()
                                    .setContent("Zone à supprimer")
                                    .addComponents(actionRow)
                                    .send(messageCreateEvent.getChannel());
                            Main.getButtonsManager().addButton(id, new Consumer<MessageComponentCreateEvent>() {
                                @Override
                                public void accept(MessageComponentCreateEvent messageComponentCreateEvent) {
                                    MessageComponentInteraction mci = messageComponentCreateEvent.getMessageComponentInteraction();
                                    Optional<SelectMenuInteraction> selectMenuInteraction = mci.asSelectMenuInteraction();
                                    if (selectMenuInteraction.isPresent()) {
                                        SelectMenuInteraction interaction = selectMenuInteraction.get();
                                        int index = Integer.parseInt(interaction.getChosenOptions().get(0).getLabel());

                                    }
                                }
                            });
                            // TODO: Add a listener to the message
                            break;
                        default:
                            messageCreateEvent.getMessage().reply("Action inconnue");
                            break;
                    }
                } else {
                    messageCreateEvent.getMessage().reply("Actions possibles : create_new_place, links, add_link, list");
                }
            }
        }
    }

    public void createNormalPlace(MessageCreateEvent messageCreateEvent, ServerBot serverBot, Player p) {
        if (serverBot.getArray("places").length == 1 && Objects.equals(serverBot.getArray("places")[0], "")) {
            serverPlace(messageCreateEvent, serverBot, p);
        } else {
            messageCreateEvent.getMessage().reply("Impossible : un serveur du monde normal ne peut avoir qu' un seul lieu");
        }
    }

    private void serverPlace(MessageCreateEvent messageCreateEvent, ServerBot serverBot, Player p) {
        Place place = new Place()
                .setAndGet("name", serverBot.getString("namerp"))
                .setAndGet("world", serverBot.getString("world"))
                .setAndGet("serv", String.valueOf(serverBot.getId()))
                .setAndGet("type", "server")
                .setAndGet("train", serverBot.getString("welcome"))
                .setAndGet("descr", serverBot.getString("descr"));
        messageCreateEvent.getMessage().reply(place.getPlaceEmbed());
        serverBot.set("places", String.valueOf(place.getID()));
        messageCreateEvent.getMessage().reply("Message de départ du lieu :");
        Main.getMessagesManager().setValueAndRetry(messageCreateEvent.getChannel(), p.getId(), "traout", "Message de sortie mit à jour, configuration terminée. Comment voyager vers d' autres lieux dans ce monde ? Dans ce monde les joueurs dans un serveur peuvent payer pour créer une connection (nom RP à trouver) entre 2 lieux", 1500, serverBot, () -> {
        });
    }

    public void createWorldPlace(MessageCreateEvent messageCreateEvent, ServerBot serverBot, Player p, String[] args) {
        messageCreateEvent.getMessage().reply("ATTENTION : la création d'un lieu dans ce monde est long\nContinuer ?");
        long yes = SaveLocation.generateUniqueID();
        long no = SaveLocation.generateUniqueID();
        Main.getButtonsManager().addButton(yes, messageComponentCreateEvent -> {
            if (messageComponentCreateEvent.getMessageComponentInteraction().getUser().getId() == p.getId()) {
                if (serverBot.getArray("places").length == 1 && Objects.equals(serverBot.getArray("places")[0], "")) {
                    serverPlace(messageCreateEvent, serverBot, p);
                } else {
                    // récupération du lieu parent
                    Place placeParent = getPlaceParent(serverBot);
                    PlaceZones placeZones = new PlaceZones(placeParent.getID());
                    if (args.length < 4) {
                        sendArgs(messageCreateEvent, p);
                    }
                    if (isNotNumeric(args[2])) {
                        sendNumberEx(messageCreateEvent, p, 2);
                        return;
                    }
                    if (isNotNumeric(args[3])) {
                        sendNumberEx(messageCreateEvent, p, 3);
                        return;
                    }

                    int x = Integer.parseInt(args[2]);
                    int y = Integer.parseInt(args[3]);

                    if (!placeZones.isInZones(x, y)) {
                        throw new RuntimeException("Impossible de créer un lieu dans cet emplacement : votre serveur n'a pas de zone à cet emplacement");
                    }

                    Place place = new Place()
                            .setAndGet("world", serverBot.getString("world"))
                            .setAndGet("serv", String.valueOf(serverBot.getId()))
                            .setAndGet("type", "city")
                            .setAndGet("city_size", "1")
                            .setAndGet("x", args[2])
                            .setAndGet("y", args[3]);
                    messageCreateEvent.getMessage().reply(place.getPlaceEmbed());
                    serverBot.set("places", String.valueOf(place.getID()));
                    messageCreateEvent.getMessage().reply("Message de départ du lieu :");
                    Main.getMessagesManager().setValueAndRetry(messageCreateEvent.getChannel(), p.getId(), "traout", "Message de sortie mit à jour. Message d'arrivée du lieu :", 1500, serverBot,
                            () -> Main.getMessagesManager().setValueAndRetry(messageCreateEvent.getChannel(), p.getId(), "train", "Message d'arrivée du lieu mit à jour. Nom du lieu :", 1500, serverBot,
                                    () -> Main.getMessagesManager().setValueAndRetry(messageCreateEvent.getChannel(), p.getId(), "name", "Nom du lieu mit à jour. Description du lieu :", 1500, serverBot,
                                            () -> Main.getMessagesManager().setValueAndRetry(messageCreateEvent.getChannel(), p.getId(), "descr", "Description du lieu mit à jour. Configuration terminée pour cette ville.", 1500, serverBot, () -> {
                                            }))));
                    // fin de la configuration des villes
                }
            }
        });
        Main.getButtonsManager().addButton(no, messageComponentCreateEvent -> {
            if (messageComponentCreateEvent.getMessageComponentInteraction().getUser().getId() == p.getId()) {
                messageComponentCreateEvent.getMessageComponentInteraction().getMessage().ifPresent(Message::delete);
            }
        });
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setContent("Réponse :");
        messageBuilder.addComponents(ActionRow.of(
                Button.success(String.valueOf(yes), "Oui"),
                Button.danger(String.valueOf(no), "Non")
        ));
    }

    public String placesToString(ArrayList<Place> places) {
        if (places.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder()
                .append(places.get(0).getID());
        for (int i = 1; i < places.size(); i++) {
            builder.append(";")
                    .append(places.get(i));
        }
        return builder.toString();
    }

    public void setPlaceEmbed(EmbedBuilder embedBuilder, int min, int max, ArrayList<Place> places) {
        embedBuilder
                .setTitle("Liste des lieux de " + min + " à " + max)
                .setColor(Color.ORANGE)
                .setDescription("Utilisez les boutons pour voir les autres pages");

        for (int i = min; i < max; i++) {
            Place place = places.get(i);
            embedBuilder.addField(Objects.equals(place.getString("name"), "") ? "Nom invalide" : place.getString("name"), place.getID() + " -> " + place.getString("descr"));
        }
    }

    public class EventAnswer {

        private int level = 0;
        private final EmbedBuilder builder;
        private final ArrayList<Place> places;

        public void next(MessageComponentCreateEvent messageComponentCreateEvent) {
            if (level + 10 < places.size()) {
                level += 10;
                builder.removeAllFields();
                setPlaceEmbed(builder, level, Math.min(places.size(), level + 11), places);
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().addEmbed(builder).addComponents(getComponents()).update();
            }
        }

        public void last(MessageComponentCreateEvent messageComponentCreateEvent) {
            if (level > 9) {
                level -= 10;
                builder.removeAllFields();
                setPlaceEmbed(builder, level, Math.min(places.size(), level + 11), places);
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().addEmbed(builder).addComponents(getComponents()).update();
            }
        }

        public ActionRow getComponents() {
            if (level > 0 && level + 10 < places.size()) {
                return ActionRow.of(
                        org.javacord.api.entity.message.component.Button.success("last_page", "Page précédente"),
                        org.javacord.api.entity.message.component.Button.success("next_page", "Page suivante")
                );
            } else if (level > 0) {
                return ActionRow.of(
                        org.javacord.api.entity.message.component.Button.success("last_page", "Page précédente")
                );
            } else if (level + 10 < places.size()) {
                return ActionRow.of(
                        Button.success("next_page", "Page suivante")
                );
            } else {
                return ActionRow.of(Button.success("null_page", "Aucune autre page"));
            }
        }

        public void register(long id) {
            HashMap<String, Consumer<MessageComponentCreateEvent>> hashMap = new HashMap<>();
            hashMap.put("next_page", this::next);
            hashMap.put("last_page", this::last);
            Main.getButtonsManager().addMessage(id, hashMap);
        }

        public EventAnswer(EmbedBuilder embedBuilder, ArrayList<Place> places) {
            builder = embedBuilder;
            this.places = places;
        }
    }

    public Place getPlaceParent(ServerBot serverBot) {
        Place placeParent = null;
        for (String placeID : serverBot.getArray("places")) {
            try {
                Place place = Main.getSaveManager().places.get(Long.parseLong(placeID));
                if (place != null && place.getString("type").equals("server")) {
                    placeParent = place;
                }
            } catch (Exception ignored) {

            }
        }
        if (placeParent == null) {
            throw new RuntimeException("Impossible de trouver le lieu parent.");
        }
        return placeParent;
    }
}
