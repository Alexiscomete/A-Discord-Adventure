package io.github.alexiscomete.lapinousecond.useful.transactions

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.resources.Resource

interface Transaction {
    val who: Player
    val amount: Double
    val what: Resource
    val amountRB: Double
}