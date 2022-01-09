package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.ListenerMain;
import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Help extends CommandBot {

    public Help() {
        super("Vous affiche l'aide du bot. (help [commande] pour plus d'informations)", "help", "Vous affiche l'aide du bot. (help [commande] pour plus d'informations)");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("Pensez au préfixe !").setTitle("Aide").setColor(Color.blue);
        if (args.length < 2) {
            MessageBuilder messageBuilder = new MessageBuilder();
            addCommands(builder, 0);
            EventAnswer eventAnswer = new EventAnswer(builder);
            messageBuilder.addComponents(eventAnswer.getComponents());
            messageBuilder.setEmbed(builder);
            try {
                eventAnswer.register(messageBuilder.send(messageCreateEvent.getChannel()).get().getId());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            CommandBot commandBot = ListenerMain.commands.get(args[1]);
            if (commandBot == null) {
                builder.addField("👀", "Commande inconnue");
            } else {
                builder.addField(commandBot.getName(), commandBot.getTotalDescription());
            }
            messageCreateEvent.getMessage().reply(builder);
        }
    }

    public void addCommands(EmbedBuilder embedBuilder, int min) {
        CommandBot[] commandBots = ListenerMain.commands.values().toArray(new CommandBot[0]);
        if (commandBots.length > min) {
            for (int i = min; i < commandBots.length && i < min + 10; i++) {
                CommandBot commandBot = commandBots[i];
                embedBuilder.addField(commandBot.getName(), commandBot.getDescription());
            }
        } else {
            embedBuilder.addField("Erreur", "Impossible de trouver la commande n°" + min);
        }
    }

    public class EventAnswer {

        private int level = 0;
        private final EmbedBuilder builder;

        public void next(MessageComponentCreateEvent messageComponentCreateEvent) {
            if (level + 10 < ListenerMain.commands.size()) {
                level += 10;
                builder.removeAllFields();
                addCommands(builder, level);
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().addEmbed(builder).addComponents(getComponents()).update();
            }
        }

        public void last(MessageComponentCreateEvent messageComponentCreateEvent) {
            if (level > 9) {
                level -= 10;
                builder.removeAllFields();
                addCommands(builder, level);
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().addEmbed(builder).addComponents(getComponents()).update();
            }
        }

        public ActionRow getComponents() {
            if (level > 0 && level + 10 < ListenerMain.commands.size()) {
                return ActionRow.of(
                        Button.success("last_page", "Page précédente"),
                        Button.success("next_page", "Page suivante")
                );
            } else if (level > 0) {
                return ActionRow.of(
                        Button.success("last_page", "Page précédente")
                );
            } else if (level + 10 < ListenerMain.commands.size()) {
                return ActionRow.of(
                        Button.success("next_page", "Page suivante")
                );
            } else {
                return ActionRow.of();
            }
        }

        public void register(long id) {
            HashMap<String, Consumer<MessageComponentCreateEvent>> hashMap = new HashMap<>();
            hashMap.put("next_page", this::next);
            hashMap.put("last_page", this::last);
            Main.getButtonsManager().addMessage(id, hashMap);
        }

        public EventAnswer(EmbedBuilder embedBuilder) {
            builder = embedBuilder;
        }
    }
}
