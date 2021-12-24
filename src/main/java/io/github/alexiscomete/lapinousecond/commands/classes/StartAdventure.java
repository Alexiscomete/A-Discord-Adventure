package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import io.github.alexiscomete.lapinousecond.save.Tables;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
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
                    HashMap<String, String> what = new HashMap<>();
                    Verify.UserData userData = Verify.getUserData(messageCreateEvent.getMessageAuthor().getId());
                    if (userData.hasAccount()) {
                        if (userData.isVerify()) {
                            messageCreateEvent.getMessage().reply("Votre compte va être associé à votre pixel. Vous avez la vérification");
                        } else {
                            messageCreateEvent.getMessage().reply("Votre compte va être associé à votre pixel. Vous n'avez malheuresement pas la vérification 😕");
                        }
                    }
                    what.put("x", String.valueOf(userData.getX()));
                    what.put("y", String.valueOf(userData.getY()));
                    what.put("hasAccount", SaveManager.toBooleanString(userData.hasAccount()));
                    what.put("isVerify", SaveManager.toBooleanString(userData.isVerify()));
                    User user = msga.get();
                    what.put("id", String.valueOf(user.getId()));
                    what.put("bal", String.valueOf(0));
                    what.put("serv", String.valueOf(854288660147994634L));
                    what.put("tuto", String.valueOf(1));
                    saveManager.insert("players", what);
                    p = new Player(user.getId(), 0L, 854288660147994634L, (short) 1);
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
