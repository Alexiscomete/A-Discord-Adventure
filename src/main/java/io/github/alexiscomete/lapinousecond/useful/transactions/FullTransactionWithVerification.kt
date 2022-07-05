package io.github.alexiscomete.lapinousecond.useful.transactions

import io.github.alexiscomete.lapinousecond.entity.Owner
import io.github.alexiscomete.lapinousecond.resources.Resource

open class FullTransactionWithVerification(
    owner0: Owner,
    owner1: Owner?,
    amount0: Double?,
    ressource0: Resource?,
    amount1: Double?,
    ressource1: Resource?,
    isValid0: Boolean,
    isValid1: Boolean,
) : TransactionWithVerification(owner0, owner1, amount0, ressource0, amount1, ressource1, isValid0, isValid1) {

    fun askAmounts() {
        // si un montant est null, on demande le montant. Attention : l'owner doit être défini avant
        var count = 0

        if (owner1 == null) {
            throw IllegalArgumentException("owner1 must be defined")
        }

        if (amount1 == null) {
            count++
        }

        if (amount0 == null) {
            count++
            owner0.askAmount(owner1!!) {
                count--
                amount0 = it
                if (count == 0) {
                    askValidation()
                }
            }
        }

        if (amount1 == null && owner1 != null) {
            owner1!!.askAmount(owner0) {
                count--
                amount1 = it
                if (count == 0) {
                    askValidation()
                }
            }
        }
    }

    fun askRessources() {
        var count = 0

        if (owner1 == null) {
            throw IllegalArgumentException("owner1 must be defined")
        }

        if (ressource1 == null) {
            count++
        }

        if (ressource0 == null) {
            count++
            owner0.askRessource(owner1!!) {
                count--
                ressource0 = it
                if (count == 0) {
                    askAmounts()
                }
            }
        }

        if (ressource1 == null && owner1 != null) {
            owner1!!.askRessource(owner0) {
                count--
                ressource1 = it
                if (count == 0) {
                    askAmounts()
                }
            }
        }
    }
}