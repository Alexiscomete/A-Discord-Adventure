package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
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
                    //TODO modifier lore
                    messageCreateEvent.getMessage().reply("*Vous vous réveillez un matin après un rêve sur le Wumpus d'or. Vous décidez de partir à la recherche de cette légende ...*\nBienvenue dans A Discord Adventure !\nPrêt vivre une aventure se déroulant sur plusieurs serveurs ? Le principe est simple : il existe une histoire principale commune à tout les serveurs, mais chaque serveur peut aussi avoir sa propre histoire plus ou moins configurable ! Les textes RP serons le plus souvent en *italique*. Vous pouvez voyager **de serveur en serveur** quand le bot vous envoie une **invitation**, le plus souvent après avoir **acheté** par exemple un **ticket** pour voyager sur un bateau !\nLes serveurs sont uniquement sur le thème de la **RPDB**, et le bot ne peut être configuré que par des **personnes autorisées**. Si vous voyez malgré tout un abus signalez le sur le **serveur principal du bot**. Commençont le tuto ... tapez la commande `ìnv`");
                }
            }
        } else {
            messageCreateEvent.getMessage().reply("La reprise du tutoriel après le début de la partie n'est pas encore disponible !");
        }
    }
}
