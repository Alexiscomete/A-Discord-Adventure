package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.invite.InviteBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class  Travel extends CommandInServer {

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
            travelWorldNormal(messageCreateEvent,args, p);
        } else if (Objects.equals(world, "DIBIMAP")) {
            travelWorldDibimap(messageCreateEvent,args, p);
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
        // TODO : monde avec coordonnées
        if (Objects.equals(placeID, "")) { // si le lieu est vide alors on l'initialise au lieu de départ : le serveur A Discord Adventure
            placeID = new ServerBot(854288660147994634L).getString("places");
            p.set("place_NORMAL", new ServerBot(854288660147994634L).getString("places"));
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
        // TODO : monde avec coordonnées : déplacement libre
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

        // On regarde si le joueur a assez d'argent pour se rendre à la destination TODO : monde avec coordonnées
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

    private void travelWorldDibimap(MessageCreateEvent messageCreateEvent, String[] args, Player p) {
    }

}
