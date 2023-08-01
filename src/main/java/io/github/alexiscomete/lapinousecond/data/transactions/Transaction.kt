package io.github.alexiscomete.lapinousecond.data.transactions

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerData
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource

interface Transaction {
    val who: PlayerData
    val amount: Double
    val what: Resource
    val amountRB: Double
}