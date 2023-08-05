package io.github.alexiscomete.lapinousecond.data.transactions

import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager

interface Transaction {
    val who: PlayerManager
    val amount: Double
    val what: Resource
    val amountRB: Double
}