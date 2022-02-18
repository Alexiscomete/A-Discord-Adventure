package io.github.alexiscomete.lapinousecond.useful;

import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.view.AnswerEnum;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class VerifTransaction extends Transaction {

    Player p;
    TextChannel textChannel;

    public VerifTransaction(Consumer<Double> addMoney, Consumer<Double> removeMoney, Supplier<Double> getMoney, Player p, TextChannel textChannel) {
        super(addMoney, removeMoney, getMoney);
        this.p = p;
        this.textChannel = textChannel;
    }

    public void ask(Double quantity) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(p.getAnswer(AnswerEnum.CONFIRMATION, true))
                .setColor(Color.BLUE)
                .setDescription("");
        MessageBuilder messageBuilder = new MessageBuilder();
    }
}
