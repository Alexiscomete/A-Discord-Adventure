package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import io.github.alexiscomete.lapinousecond.worlds.map.Map;
import io.github.alexiscomete.lapinousecond.worlds.map.Pixel;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.invite.InviteBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Travel extends CommandInServer {

    public Travel() {
        super("Vous permet de voyager vers un serveur", "travel", "travel [server id]");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {

        // On explique au joueur qu'il manque des arguments : le lieu vers lequel il veut se rendre
        if (args.length < 2 || args[1].equals("list")) {
            messageCreateEvent.getMessage().reply("Utilisez place links pour voir les possibilités de voyage, si aucune ne vous convient tentez le create_link pour le monde NORMAL");
            return;
        }

        // On récupère le monde du joueur
        String world = p.getString("current_world");
        if (Objects.equals(world, "")) { // si le monde est vide alors on le met à NORMAL
            world = "NORMAL";
            p.set("current_world", "NORMAL");
        }

        if (Objects.equals(world, "NORMAL")) {
            travelWorldNormal(messageCreateEvent, args, p);
        } else if (Objects.equals(world, "DIBIMAP")) {
            travelWorldDibimap(messageCreateEvent, args, p);
        }
    }

    // --------------------------------------------------
    // --------- EN FONCTION DU MONDE -------------------
    // --------------------------------------------------

    /**
     * Si le monde est le monde est le monde normal
     */
    private void travelWorldNormal(MessageCreateEvent messageCreateEvent, String[] args, Player p) {
        // On récupère le lieu du joueur dans le monde
        String placeID = p.getString("place_NORMAL");
        if (Objects.equals(placeID, "")) { // si le lieu est vide alors on l'initialise au lieu de départ : le serveur A Discord Adventure
            placeID = new ServerBot(854288660147994634L).getString("places");
            p.set("place_NORMAL", placeID);
        }

        // On récupère le lieu du joueur sous forme d'objet avec l'id
        Place place = saveManager.places.get(Long.parseLong(placeID));

        // si le lieu n'existe pas on l'indique au joueur et on propose d'utiliser -hub
        if (place == null) {
            System.out.println(placeID);
            messageCreateEvent.getMessage().reply("Votre lieu est introuvable, utilisez -hub pour revenir au spawn.");
            return;
        }

        // on recupère les lieux dans lesquels le joueur peut se rendre depuis le lieu actuel
        ArrayList<Place> places = Place.toPlaces(place.getString("connections"));

        // On récupère le serveur dans lequel le joueur veut se rendre
        Place dest;
        try {
            dest = new Place(Long.parseLong(args[1]));
        } catch (IllegalArgumentException e) {
            messageCreateEvent.getMessage().reply("SVP ne jouez pas à entrer autre chose que des nombres");
            return;
        }

        // On vérifie les liens entre les deux lieux
        if (!places.contains(dest)) {
            messageCreateEvent.getMessage().reply("Il n' existe pas de route entre votre lieu et votre destination");
            return;
        }

        // On regarde si le joueur a assez d'argent pour se rendre à la destination
        double bal = p.getBal();
        if (bal < 100) {
            messageCreateEvent.getMessage().reply("Il vous faut 100 rb pour voyager dans le monde normal");
            return;
        }

        // on récupère le serveur discord de destinatione en optionnel
        Optional<Server> serverOp = Main.api.getServerById(Long.parseLong(dest.getString("serv")));
        if (serverOp.isEmpty()) { // si le serveur n'existe pas (ou que le bot n'a pas l'accès) on l'indique au joueur
            messageCreateEvent.getMessage().reply("Voyage vers cette destination impossible : le serveur discord est inconnu. Possibilités : bot kick, serveur supprimé, corruption du lieu");
            return;
        }

        // on récupère le serveur discord de destination
        Server server = serverOp.get();

        // message de confirmation
        if (args.length < 3) {
            messageCreateEvent.getMessage().reply("Prix pour aller dans ce serveur : 100. Tapez la même commande avec oui à la fin pour confirmer votre choix (ce dernier est irrévocable)");
            return;
        }

        // on récupère les salons de discussion du serveur
        List<ServerChannel> channels = server.getChannels();
        channels = channels.stream().filter(serverChannel -> serverChannel.getType().isTextChannelType()).collect(Collectors.toList());
        if (channels.size() == 0) { // si on ne trouve pas de salon de discussion on l'indique au joueur et au propriétaire du serveur
            messageCreateEvent.getMessage().reply("Bon je pense que ce serveur ne vaux pas la peine : il n'y aucun salon !! Je ne peux même pas vous inviter.");
            server.getOwner().get().sendMessage("Bon ... si il n'y a même pas de salon dans votre serveur je ne peux rien faire.");
            return;
        }

        // on récupère le serveur de destination sous forme d'objet personnalisé
        ServerBot nextServer = saveManager.servers.get(Long.parseLong(dest.getString("serv")));

        // on regarde si le serveur est bien enregistré dans la base de données
        if (nextServer == null) {
            messageCreateEvent.getMessage().reply("Voyage impossible : le serveur n' est pas configuré");
            return;
        }

        // création de l'invitation vers le serveur
        InviteBuilder inv = new InviteBuilder(channels.get(0));
        System.out.println(channels.get(0).getName());
        try {

            // on envoie l'invitation après avoir récupéré l'utilisateur
            User user = messageCreateEvent.getMessageAuthor().asUser().get();
            user.sendMessage(inv.create().get().getUrl().toString());
            user.sendMessage(dest.getString("train"));

            // on set les valeurs dans la base de données
            p.setServer(nextServer.getId());
            p.setBal(bal - 100);
            p.set("place_NORMAL", String.valueOf(dest.getID()));

            // on envoie un message de confirmation
            messageCreateEvent.getMessage().reply("Dans le monde NORMAL le voyage est instantané, au revoir !");
        } catch (InterruptedException | ExecutionException e) {
            // on envoie un message d'erreur
            messageCreateEvent.getMessage().reply("Une erreur est survenue lors de la création de l'invitation.");
            e.printStackTrace();
        }
    }

    // si le joueur est dans le monde Dibimap
    private void travelWorldDibimap(MessageCreateEvent messageCreateEvent, String[] args, Player p) {
        // on récupère le lieu dans le monde Dibimap
        String placeType = p.getString("place_DIBIMAP_type");
        if (placeType == null) {
            placeType = "coos";
            setCoos(p);
        }
        // je regarde si les arguments sont bien présents
        if (args.length < 2) {
            // si non on envoie un message d'erreur car il n'a pas donné le type de voyage
            sendArgs(messageCreateEvent, p);
            return;
        }
        // je sépare en 2 cas : si le type est coos ou si le type est un lieu
        if (placeType.equals("coos")) {
            // on récupère les coordonnées
            int x = Integer.parseInt(p.getString("place_DIBIMAP_x"));
            int y = Integer.parseInt(p.getString("place_DIBIMAP_y"));
            // on ne récupère pas le lieu, car on ne connait pas le lieu
            // on sépare le cas où le joueur veut aller dans un lieu et celui où il veut aller sur des coos

            // création du menu
            MessageBuilder mb = new MessageBuilder();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Voyage dans le monde Dibimap");
            eb.setDescription("Vous êtes dans le monde Dibimap en dehors d'une ville, vous pouvez choisir entre aller dans un lieu ou sur des coos.");
            eb.setColor(Color.BLUE);
            mb.append(eb);

            long id1 = SaveLocation.generateUniqueID();
            long id2 = SaveLocation.generateUniqueID();

            // ajout des choix
            mb.addComponents(ActionRow.of(
                    Button.success(String.valueOf(id1), "Aller dans un lieu"),
                    Button.success(String.valueOf(id2), "Aller sur des coos")
            ));

            // ajout des actions
            Main.getButtonsManager().addButton(id1, messageComponentCreateEvent -> {
                // on récupère le lieu en demandant à l'utilisateur de le rentrer
                TextChannel tc = messageCreateEvent.getMessage().getChannel();
                long userId = messageCreateEvent.getMessage().getAuthor().getId();
                messageComponentCreateEvent.getMessageComponentInteraction().createImmediateResponder().setContent("Entrez le nom du lieu :").respond();
                Main.getMessagesManager().addListener(tc, userId, (messageCreateEvent1) -> {
                    // on récupère le lieu
                    String place = messageCreateEvent1.getMessage().getContent();
                    // on récupère le lieu
                    try {
                        Place placeO = Main.getSaveManager().places.get(Long.parseLong(place));
                        if (placeO == null) {
                            throw new IllegalArgumentException("Lieu introuvable");
                        }
                        messageCreateEvent1.getMessage().reply("Calcul du trajet en cours ....");
                        ArrayList<Pixel> path = Map.findPath(Map.getNode(x, y, new ArrayList<>()), Map.getNode(Integer.parseInt(placeO.getString("x")), Integer.parseInt(placeO.getString("y")), new ArrayList<>()), messageCreateEvent.getChannel());
                        StringBuilder sb = new StringBuilder();
                        for (Pixel pixel : path) {
                            sb.append(pixel);
                        }
                        messageCreateEvent.getMessage().reply(sb.toString());
                        MessageBuilder messageBuilder3 = new MessageBuilder();
                        messageBuilder3.addAttachment(Map.drawPath(path), "path.png");
                        messageBuilder3.send(messageCreateEvent.getChannel());
                        long timeMillisToTravel = path.size() * 10000L;
                        double priceToTravel = path.size() * 0.5;
                        // on indique le temps de trajet ou le prix que cela peut lui couter en fonction du nombre de pixels de trajet
                        messageCreateEvent.getMessage().reply("Vous allez à " + placeO.getString("name") + " en " + timeMillisToTravel + " millisecondes ou " + priceToTravel + " rb.");
                        long id3 = SaveLocation.generateUniqueID(), id4 = SaveLocation.generateUniqueID();
                        new MessageBuilder()
                                .setEmbed(new EmbedBuilder()
                                        .setTitle("Vous allez à " + placeO.getString("name"))
                                        .setDescription("Avec " + path.size() + " pixels de trajet en " + timeMillisToTravel + " millisecondes ou " + priceToTravel + " rb.")
                                        .setImage(Map.drawPath(path), "path.png")
                                        .setColor(Color.GREEN))
                                .addComponents(ActionRow.of(
                                        Button.success(String.valueOf(id3), "Temps de trajet"),
                                        Button.success(String.valueOf(id4), "Prix de trajet")))
                                .send(messageCreateEvent.getChannel());
                        int state = p.state;
                        Main.getButtonsManager().addButton(id3, (messageButtonEvent) -> {
                            verifButton(p, state, messageButtonEvent);

                            // on ajoute le chemin au joueur avec pour type "default_time"
                            p.setPath(path, "default_time");

                            // on envoie le message de confirmation
                            messageButtonEvent.getMessageComponentInteraction().createImmediateResponder().setContent("Vous avez commencé votre trajet pour aller à " + placeO.getString("name") + " en " + timeMillisToTravel / 1000 + " secondes.").respond();
                        });
                        Main.getButtonsManager().addButton(id4, (messageButtonEvent) -> {
                            verifButton(p, state, messageButtonEvent);

                            double bal = p.getBal();
                            if (bal < priceToTravel) {
                                throw new IllegalStateException("Vous n'avez pas assez de rb pour ce trajet");
                            }
                            p.setBal(bal - priceToTravel);

                            // il a payé donc on téléporte le joueur
                            p.set("place_DIBIMAP_type", "place");
                            p.set("place_DIBIMAP_id", String.valueOf(id2));
                            p.set("place_DIBIMAP_x", placeO.getString("x"));
                            p.set("place_DIBIMAP_y", placeO.getString("y"));

                            // on dit au joueur qu'il est téléporté
                            messageButtonEvent.getMessageComponentInteraction().createImmediateResponder().setContent("Vous avez été téléporté à " + placeO.getString("name") + " car vous avez payé " + priceToTravel + " rb.").respond();
                        });
                    } catch (NumberFormatException e) {
                        // on envoie un message d'erreur
                        sendNumberEx(messageCreateEvent1, p, -1);
                    }
                });
            });
            Main.getButtonsManager().addButton(id2, messageComponentCreateEvent -> {
                // on récupère les coordonnées où le joueur souhaite aller
                TextChannel tc = messageCreateEvent.getMessage().getChannel();
                long userId = messageCreateEvent.getMessage().getAuthor().getId();
                messageComponentCreateEvent.getMessageComponentInteraction().createImmediateResponder().setContent("Entrez les coordonnées de la destination :").respond();
                Main.getMessagesManager().addListener(tc, userId, (messageCreateEvent1) -> {
                    // on récupère le message
                    String message = messageCreateEvent1.getMessage().getContent();

                    // les coordonnées sont séparées par un espace
                    String[] coords = message.split(" ");
                    if (coords.length != 2) {
                        // on envoie un message d'erreur
                        throw new IllegalStateException("Vous devez entrer les coordonnées de la destination séparées par un espace.");
                    }

                    // on vérifie que les coordonnées sont bien des nombres
                    if (isNotNumeric(coords[0]) || isNotNumeric(coords[1])) {
                        // on envoie un message d'erreur
                        throw new IllegalStateException("Les coordonnées doivent être des nombres.");
                    }

                    // on récupère les coordonnées
                    int xDest = Integer.parseInt(coords[0]);
                    int yDest = Integer.parseInt(coords[1]);

                    // on vérifie les bornes
                    if (xDest < 0 || xDest >= Map.MAP_WIDTH || yDest < 0 || yDest >= Map.MAP_HEIGHT) {
                        // on envoie un message d'erreur
                        throw new IllegalStateException("Les coordonnées doivent être comprises entre 0 et " + (Map.MAP_WIDTH - 1) + " et " + (Map.MAP_HEIGHT - 1) + ".");
                    }

                    messageCreateEvent1.getMessage().reply("Calcul du trajet en cours ....");
                    ArrayList<Pixel> path = Map.findPath(Map.getNode(x, y, new ArrayList<>()), Map.getNode(xDest, yDest, new ArrayList<>()), messageCreateEvent.getChannel());
                    StringBuilder sb = new StringBuilder();
                    for (Pixel pixel : path) {
                        sb.append(pixel);
                    }
                    messageCreateEvent.getMessage().reply(sb.toString());
                    MessageBuilder messageBuilder3 = new MessageBuilder();
                    messageBuilder3.addAttachment(Map.drawPath(path), "path.png");
                    messageBuilder3.send(messageCreateEvent.getChannel());
                    long timeMillisToTravel = path.size() * 10000L;
                    double priceToTravel = path.size() * 0.5;
                    // on indique le temps de trajet ou le prix que cela peut lui couter en fonction du nombre de pixels de trajet
                    messageCreateEvent.getMessage().reply("Vous allez en [" + x + ":" + y + "] en " + timeMillisToTravel + " millisecondes ou " + priceToTravel + " rb.");
                    long id3 = SaveLocation.generateUniqueID(), id4 = SaveLocation.generateUniqueID();
                    new MessageBuilder()
                            .setEmbed(new EmbedBuilder()
                                    .setTitle("Vous allez en [" + x + ":" + y + "]")
                                    .setDescription("Avec " + path.size() + " pixels de trajet en " + timeMillisToTravel + " millisecondes ou " + priceToTravel + " rb.")
                                    .setImage(Map.drawPath(path), "path.png")
                                    .setColor(Color.GREEN))
                            .addComponents(ActionRow.of(
                                    Button.success(String.valueOf(id3), "Temps de trajet"),
                                    Button.success(String.valueOf(id4), "Prix de trajet")))
                            .send(messageCreateEvent.getChannel());
                    int state = p.state;
                    Main.getButtonsManager().addButton(id3, (messageButtonEvent) -> {
                        verifButton(p, state, messageButtonEvent);

                        // on ajoute le chemin au joueur avec pour type "default_time"
                        p.setPath(path, "default_time");

                        // on envoie le message de confirmation
                        messageButtonEvent.getMessageComponentInteraction().createImmediateResponder().setContent("Vous avez commencé votre trajet pour aller en [" + x + ":" + y + "] en " + timeMillisToTravel / 1000 + " secondes.").respond();
                    });
                    Main.getButtonsManager().addButton(id4, (messageButtonEvent) -> {
                        verifButton(p, state, messageButtonEvent);

                        double bal = p.getBal();
                        if (bal < priceToTravel) {
                            throw new IllegalStateException("Vous n'avez pas assez de rb pour ce trajet");
                        }
                        p.setBal(bal - priceToTravel);

                        // il a payé donc on téléporte le joueur
                        p.set("place_DIBIMAP_id", String.valueOf(id2));
                        p.set("place_DIBIMAP_x", String.valueOf(x));
                        p.set("place_DIBIMAP_y", String.valueOf(y));

                        // on dit au joueur qu'il est téléporté
                        messageButtonEvent.getMessageComponentInteraction().createImmediateResponder().setContent("Vous avez été téléporté en [" + x + ":" + y + "] car vous avez payé " + priceToTravel + " rb.").respond();
                    });

                });
            });


        } else if (placeType.equals("place")) {
            // on récupère le lieu
            String place = p.getString("place_DIBIMAP_place");
            // on récupère le lieu dans la base de données
            Place place1 = Main.getSaveManager().places.get(Long.parseLong(place));
            // on sépare le cas où le joueur veut aller dans un lieu et celui où il veut aller sur des coos
        } else {
            // on envoie un message d'erreur
            messageCreateEvent.getMessage().reply("Etrange, ce type de lieu n'existe pas. Je vais donc vous téléporter. Retentez votre commande.");
            setCoos(p);
        }
    }

    private void verifButton(Player p, int state, MessageComponentCreateEvent messageButtonEvent) {
        MessageComponentInteraction messageComponentInteraction = messageButtonEvent.getMessageComponentInteraction();
        if (messageComponentInteraction.getUser().getId() != p.getId()) {
            throw new IllegalStateException("Ce bouton n'est pas pour vous");
        }
        if (messageComponentInteraction.getMessage().isPresent()) {
            messageComponentInteraction.getMessage().get().delete();
        }
        if (p.state != state) {
            throw new IllegalStateException("Ce bouton n'est plus valide");
        }
        p.state++;
    }

    // set coordonnées par défaut
    private void setCoos(Player p) {
        // on met les coordonnées par défaut
        p.set("place_DIBIMAP_x", "400");
        p.set("place_DIBIMAP_y", "250");
        // et le type de lieu par défaut
        p.set("place_DIBIMAP_type", "coos");
    }
}
