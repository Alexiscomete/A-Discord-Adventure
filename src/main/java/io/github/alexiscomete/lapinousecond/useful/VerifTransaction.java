package io.github.alexiscomete.lapinousecond.useful;

import io.github.alexiscomete.lapinousecond.entity.Player;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;

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
        MessageBuilder messageBuilder = new MessageBuilder();
    }
}
