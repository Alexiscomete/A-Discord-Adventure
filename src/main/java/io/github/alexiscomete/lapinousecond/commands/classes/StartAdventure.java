package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.SaveManager;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public class StartAdventure extends CommandBot {

    public StartAdventure() {
        super("Permet de commencer l'aventure", "start", "Vous permet de créer votre compte sur le bot et de commencer l'aventure avec un tuto, vous pouvez réexécuter cette commande pour revoir le tuto (par exemple dans le cas d'une mise à jour importante)");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        SaveManager saveManager = Main.getSaveManager();
        Player p = saveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            if (messageCreateEvent.isPrivateMessage() || messageCreateEvent.getServer().get().getId() != 854288660147994634L) {
                messageCreateEvent.getMessage().reply("🙄 Ce bot propose une aventure se déroulant sur de nombreux serveurs, mais elle commence toujours sur le serveur du bot  (vous pourrez le quitter après) : <https://discord.gg/q4hVQ6gwyx>");
            } else {
                Optional<User> msga = messageCreateEvent.getMessageAuthor().asUser();
                if (msga.isPresent()) {
                    User user = msga.get();
                    p = new Player(user.getId(), 0, 854288660147994634L, (short) 1, (short) 0);
                    saveManager.addPlayer(user.getId(), 0, 854288660147994634L, (short) 1, (short) 0, 0);
                    saveManager.getPlayers().put(user.getId(), p);
                    messageCreateEvent.getMessage().reply("*Vous vous réveillez un matin après un rêve sur le Wumpus d'or, ce rêve vous attire de façon étrange. Vous décidez de partir à la recherche de votre rêve ...*\nBienvenue dans A Discord Adventure !\nPrêt vivre une aventure se déroulant sur plusieurs serveurs ?\nLe principe est simple : il existe une histoire principale commune à tout les serveurs, mais chaque serveur peut aussi avoir sa propre histoire plus ou moins configurable ! Les textes RP serons le plus souvent en italique. Vous pouvez voyager de serveur en serveur quand le bot vous envoie une invitation, le plus souvent après avoir acheté par exemple un ticket pour voyager sur un bateau !\nVous pouvez choisir de limiter le type de serveur que le bot vous envoie : officiel (comprenant des serveurs normaux mais aussi des serveurs event et passage obligatoire), vérifié régulièrement, vérifié, normal, tous. Nous vous conseillons de mettre normal ou vérifié, pour une meilleur expérience de jeu.\nVeuillez envoyer votre choix avec la commande `sec` : 1 - tous, 2 - normal, 3 - vérifié, 4 - vérifié régulièrement, 5 - officiel");
                }
            }
        } else {
            messageCreateEvent.getMessage().reply("La reprise du tutoriel après le début de la partie n'est pas encore disponible !");
        }
    }
}