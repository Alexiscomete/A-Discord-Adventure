package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class UseCommand extends CommandBot {

    public UseCommand() {
        super("Permet d'utiliser un objet", "use", "Utilisez use [name] pour utilisez l'objet nommé name, vous pouvez voir vos objets avec inv");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        Player p = SaveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            messageCreateEvent.getMessage().reply("Vous devez avoir un compte pour continuer");
            return;
        }
        if (args.length < 2) {
            messageCreateEvent.getMessage().reply("Vous devez avant tout indiquer le nom de l'item à utliser");
            return;
        }
        for (Item item : p.getItems()) {
            if (item.name.equalsIgnoreCase(name)) {
                if (item.use(messageCreateEvent, content, args, p)) {
                    p.getItems().remove(item);
                    messageCreateEvent.getMessage().reply("L'objet a été consommé !");
                }
                return;
            }
        }
    }
}
