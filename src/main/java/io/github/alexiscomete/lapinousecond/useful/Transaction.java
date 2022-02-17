package io.github.alexiscomete.lapinousecond.useful;

import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.view.AnswerEnum;
import org.javacord.api.entity.channel.TextChannel;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Transaction {

    private final Consumer<Double> addMoney, removeMoney;
    private final Supplier<Double> getMoney;


    public Transaction(Consumer<Double> addMoney, Consumer<Double> removeMoney, Supplier<Double> getMoney) {
        this.addMoney = addMoney;
        this.removeMoney = removeMoney;
        this.getMoney = getMoney;
    }

    public void make(TextChannel textChannel, Double quantity, Player player) {
        Double money = getMoney.get();
        if (money > quantity) {
            addMoney.accept(quantity);
            removeMoney.accept(quantity);
        } else {
            textChannel.sendMessage(player.getAnswer(AnswerEnum.ECHEC_TRANS, true, player.getAnswer(AnswerEnum.NO_ENOUGH_MONEY, false)));
        }
    }
}
