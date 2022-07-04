package io.github.alexiscomete.lapinousecond.useful.transactions

import io.github.alexiscomete.lapinousecond.entity.Owner
import io.github.alexiscomete.lapinousecond.resources.Resource

open class Transaction(val owner0: Owner, var owner1: Owner?, var amount0: Double?, var ressource0: Resource?, var amount1: Double?, var ressource1: Resource?) {
    open fun make() {
        if (owner1 == null || amount0 == null || ressource0 == null || amount1 == null || ressource1 == null) {
            throw IllegalArgumentException("Certains paramètres de la transaction sont invalides")
        } else {
            if (!transactionTwoDirections(owner0, owner1!!, amount0!!, ressource0!!, amount1!!, ressource1!!)) {
                throw IllegalStateException("Transaction impossible car les montants sont trop élevés")
            }
        }
    }
}