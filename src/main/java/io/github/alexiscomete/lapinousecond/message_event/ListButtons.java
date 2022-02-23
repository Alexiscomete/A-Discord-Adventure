package io.github.alexiscomete.lapinousecond.message_event;

import io.github.alexiscomete.lapinousecond.ListenerMain;
import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;

import java.util.ArrayList;

public class ListButtons<U> {
    private int level = 0;
    private final EmbedBuilder builder;
    private final ArrayList<U> uArrayList;
    private final AddContent<U> uAddContent;
    private final String idLast = String.valueOf(SaveLocation.generateUniqueID()), idNext = String.valueOf(SaveLocation.generateUniqueID());

    public void next(MessageComponentCreateEvent messageComponentCreateEvent) {
        if (level + 10 < uArrayList.size()) {
            level += 10;
            builder.removeAllFields();
            uAddContent.add(builder, level, Math.min(10, uArrayList.size()-level), uArrayList);
            messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().addEmbed(builder).addComponents(getComponents()).update();
        }
    }

    public void last(MessageComponentCreateEvent messageComponentCreateEvent) {
        if (level > 9) {
            level -= 10;
            builder.removeAllFields();
            uAddContent.add(builder, level, Math.min(10, uArrayList.size()-level), uArrayList);
            messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().addEmbed(builder).addComponents(getComponents()).update();
        }
    }

    public ActionRow getComponents() {
        if (level > 0 && level + 10 < ListenerMain.commands.size()) {
            return ActionRow.of(
                    Button.success(idLast, "Page précédente"),
                    Button.success(idNext, "Page suivante")
            );
        } else if (level > 0) {
            return ActionRow.of(
                    Button.success(idLast, "Page précédente")
            );
        } else if (level + 10 < ListenerMain.commands.size()) {
            return ActionRow.of(
                    Button.success(idNext, "Page suivante")
            );
        } else {
            return ActionRow.of();
        }
    }

    public void register(long id) {
        Main.getButtonsManager().addButton(Long.parseLong(idLast), this::last);
        Main.getButtonsManager().addButton(Long.parseLong(idNext), this::next);
    }

    public ListButtons(EmbedBuilder embedBuilder, ArrayList<U> uArrayList, AddContent<U> uAddContent) {
        this.builder = embedBuilder;
        this.uArrayList = uArrayList;
        this.uAddContent = uAddContent;
        uAddContent.add(embedBuilder, 0, Math.min(10, uArrayList.size()-level), uArrayList);
    }

    @FunctionalInterface
    public interface AddContent<U> {
        void add(EmbedBuilder embedBuilder, int min, int num, ArrayList<U> uArrayList);
    }
}
