package io.github.alexiscomete.lapinousecond.useful;

import io.github.alexiscomete.lapinousecond.entity.Player;
import org.javacord.api.entity.channel.TextChannel;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FullTransaction extends VerifTransaction {
    public FullTransaction(Consumer<Double> addMoney, Consumer<Double> removeMoney, Supplier<Double> getMoney, Player p, TextChannel textChannel) {
        super(addMoney, removeMoney, getMoney, p, textChannel);
    }
}
