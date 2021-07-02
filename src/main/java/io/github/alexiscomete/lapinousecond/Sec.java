package io.github.alexiscomete.lapinousecond;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class Sec extends CommandBot {

    public Sec() {
        super("Serveurs autorisés", "sec", "Veuillez envoyer votre choix avec la commande `sec` : 1 - tous, 2 - normal, 3 - vérifié, 4 - vérifié régulièrement, 5 - officiel");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        try {
            short s = Short.parseShort(args[1]);
            messageCreateEvent.getMessage().reply("✔");
            Player p = SaveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
            if (p == null) {
                messageCreateEvent.getMessage().reply("Par contre il faut vous créer un compte ... 🙄");
                return;
            }
            p.getSecurity();
            if (p.getTuto() == 1) {
                p.setTuto((short) 2);
                User user = messageCreateEvent.getMessageAuthor().asUser().get();
                user.sendMessage("Ce choix n'est pas définitif. Pour garantir une meilleur expérience de jeu, n'hésitez pas à nous signaler un serveur qui vous empêche de continuer l'aventure dans de bonnes conditions. Continuons le tuto. Vous pouvez utiliser la commande `inv` pour voir votre inventaire.");
            }
        } catch (NumberFormatException e) {
            messageCreateEvent.getMessage().reply("👀");
        }
    }
}
