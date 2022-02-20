package io.github.alexiscomete.lapinousecond.useful;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.view.AnswerEnum;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FullTransaction extends VerifTransaction {
    public FullTransaction(Consumer<Double> addMoney, Consumer<Double> removeMoney, Supplier<Double> getMoney, Player p) {
        super(addMoney, removeMoney, getMoney, p);
    }

    public void askQuantity(Consumer<Double> after, TextChannel textChannel) {
        textChannel.sendMessage(p.getAnswer(AnswerEnum.ASK_MONTANT, true));
        addL(textChannel, after);
    }

    public void askQuantity(Consumer<Double> after, MessageComponentInteraction messageComponentInteraction) {
        messageComponentInteraction.createImmediateResponder().setContent(p.getAnswer(AnswerEnum.ASK_MONTANT, true));
        addL(messageComponentInteraction.getChannel().get(), after);
    }

    private void addL(TextChannel textChannel, Consumer<Double> after) {
        Main.getMessagesManager().addListener(textChannel, p.getId(), (messageCreateEvent) -> {
            try {
                double d = Double.parseDouble(messageCreateEvent.getMessage().getContent());
                after.accept(d);
            } catch (IllegalArgumentException e) {
                messageCreateEvent.getMessage().reply(p.getAnswer(AnswerEnum.FORM_INVALID, true));
                addL(textChannel, after);
            }
        });
    }

    public void full(TextChannel textChannel) {
        askQuantity(aDouble -> askVerif(aDouble, textChannel), textChannel);
    }

    public void full(MessageComponentInteraction messageComponentInteraction) {
        askQuantity(aDouble -> askVerif(aDouble, messageComponentInteraction.getChannel().get()), messageComponentInteraction);
    }
}
