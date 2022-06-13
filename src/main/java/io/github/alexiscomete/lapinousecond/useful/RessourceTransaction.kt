package io.github.alexiscomete.lapinousecond.useful

import io.github.alexiscomete.lapinousecond.entity.Player
import java.util.function.Consumer

class RessourceTransaction(
    addMoney: Consumer<Double>,
    player: Player,
): FullTransaction(addMoney, , player) {
}