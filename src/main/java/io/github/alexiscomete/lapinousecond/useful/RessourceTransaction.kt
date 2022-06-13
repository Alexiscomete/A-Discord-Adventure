package io.github.alexiscomete.lapinousecond.useful

import io.github.alexiscomete.lapinousecond.entity.Player
import java.util.function.Consumer
import java.util.function.Supplier

class RessourceTransaction(
    addMoney: Consumer<Double>,
    removeMoney: Consumer<Double>,
    getMoney: Supplier<Double>,
    player: Player,
    max: Supplier<Double>,
) : FullTransaction(addMoney, removeMoney, getMoney, player, max) {
}