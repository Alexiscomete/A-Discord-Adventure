package io.github.alexiscomete.lapinousecond.useful;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FullTransaction extends VerifTransaction {
    public FullTransaction(Consumer<Double> addMoney, Consumer<Double> removeMoney, Supplier<Double> getMoney) {
        super(addMoney, removeMoney, getMoney);
    }
}
