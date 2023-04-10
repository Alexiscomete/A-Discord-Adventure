package io.github.alexiscomete.lapinousecond.useful.transactions

import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource

interface Transaction {
    val who: Player
    val amount: Double
    val what: Resource
    val amountRB: Double
}