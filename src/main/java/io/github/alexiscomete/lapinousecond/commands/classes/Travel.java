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

public class Travel extends CommandInServer {

    public Travel() {
        super("Vous permet de voyager vers un serveur", "travel", "travel [server id]");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {

        if (args.length < 2 || args[1].equals("list")) {
            messageCreateEvent.getMessage().reply("Utilisez place links pour voir les possibilités de voyage, si aucune ne vous convient tentez le create_link pour le monde NORMAL");
            return;
        }

        String world = p.getString("current_world");
        if (Objects.equals(world, "")) {
            world = "NORMAL";
            p.set("current_world", "NORMAL");
        }
        String placeID = p.getString("place_" + world);
        if (Objects.equals(placeID, "")) {
            placeID = new ServerBot(854288660147994634L).getString("places");
            p.set("place_NORMAL", new ServerBot(854288660147994634L).getString("places"));
        }

        Place place = saveManager.getPlace(Long.parseLong(placeID));

        if (place == null) {
            System.out.println(placeID);
            messageCreateEvent.getMessage().reply("Votre lieu est introuvable, utilisez -hub pour revenir au spawn.");
            return;
        }

        ArrayList<Place> places = Place.toPlaces(place.getString("connections"));

        Place dest;

        try {
            dest = new Place(Long.parseLong(args[1]));
        } catch (IllegalArgumentException e) {
            messageCreateEvent.getMessage().reply("SVP ne jouez pas à entrer autre chose que des nombres");
            return;
        }

        if (!places.contains(dest)) {
            messageCreateEvent.getMessage().reply("Il n' existe pas de route entre votre lieu et votre destination");
            return;
        }

        double bal = p.getBal();

        if (bal < 100) {
            messageCreateEvent.getMessage().reply("Il vous faut 100 rb pour voyager dans le monde normal");
            return;
        }


        Optional<Server> serverOp = Main.api.getServerById(Long.parseLong(dest.getString("serv")));

        if (!serverOp.isPresent()) {
            messageCreateEvent.getMessage().reply("Voyage vers cette destination impossible : le serveur discord est inconnu. Possibilités : bot kick, serveur supprimé, corruption du lieu");
            return;
        }


        Server server = serverOp.get();

        if (args.length < 3) {
            messageCreateEvent.getMessage().reply("Prix pour aller dans ce serveur : 100. Tapez la même commande avec oui à la fin pour confirmer votre choix (ce dernier est irrévocable)");
            return;
        }

        List<ServerChannel> channels = server.getChannels();
        channels = channels.stream().filter(serverChannel -> serverChannel.getType().isTextChannelType()).collect(Collectors.toList());
        if (channels.size() == 0) {
            messageCreateEvent.getMessage().reply("Bon je pense que ce serveur ne vaux pas la peine : il n'y aucun salon !! Je ne peux même pas vous inviter.");
            server.getOwner().get().sendMessage("Bon ... si il n'y a même pas de salon dans votre serveur je ne peux rien faire.");
            return;
        }

        ServerBot nextServer = saveManager.servers.get(Long.parseLong(dest.getString("serv")));

        if (nextServer == null) {
            messageCreateEvent.getMessage().reply("Voyage impossible : le serveur n' est pas configuré");
            return;
        }

        InviteBuilder inv = new InviteBuilder(channels.get(0));
        System.out.println(channels.get(0).getName());
        try {

            User user = messageCreateEvent.getMessageAuthor().asUser().get();

            user.sendMessage(inv.create().get().getUrl().toString());
            user.sendMessage(dest.getString("train"));

            p.setServer(nextServer.getId());
            p.setBal(bal - 100);
            p.set("place_" + world, String.valueOf(dest.getID()));

            messageCreateEvent.getMessage().reply("Dans le monde NORMAL le voyage est instantané, au revoir !");
        } catch (InterruptedException | ExecutionException e) {
            messageCreateEvent.getMessage().reply("Une erreur est survenue lors de la création de l'invitation.");
            e.printStackTrace();
        }
    }
}
