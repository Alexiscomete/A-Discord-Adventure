package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Item;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

public class UseCommand extends CommandBot {

    public UseCommand() {
        super("Permet d'utiliser un objet", "use", "Utilisez use [name] pour utilisez l'objet nommé name, vous pouvez voir vos objets avec inv");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        Player p = saveManager.players.get(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            messageCreateEvent.getMessage().reply("Vous devez avoir un compte pour continuer");
            return;
        }
        if (args.length < 2) {
            messageCreateEvent.getMessage().reply("Vous devez avant tout indiquer le nom de l'item à utliser");
            return;
        }
        for (Item item : p.getItems()) {
            if (item.name.equalsIgnoreCase(getName())) {
                if (item.use(messageCreateEvent, content, args, p)) {
                    p.getItems().remove(item);
                    messageCreateEvent.getMessage().reply("L'objet a été consommé !");
                }
                return;
            }
        }
    }
}
