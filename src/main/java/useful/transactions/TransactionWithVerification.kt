package useful.transactions

import entity.Owner
import resources.Resource

open class TransactionWithVerification(
    owner0: Owner, owner1: Owner?, amount0: Double?, ressource0: Resource?, amount1: Double?, ressource1: Resource?, var isValid0: Boolean, var isValid1: Boolean
) : Transaction(
    owner0, owner1, amount0, ressource0, amount1, ressource1
) {

    // je dois vérifier que les transactions ont été acceptées par les joueurs avant de pouvoir les valider
    override fun make() {
        if (isValid()) {
            super.make()
        } else {
            throw IllegalStateException("Les propriétaires n'ont pas accepté les transactions")
        }
    }

    // vérifier la validité des transactions
    fun isValid(): Boolean {
        return isValid0 && isValid1
    }

    // demander aux propriétaires de valider la transaction si ce n'est pas déjà fait
    fun askValidation() {
        if (owner1 == null || amount0 == null || ressource0 == null || amount1 == null || ressource1 == null) {
            throw IllegalStateException("Les transactions doivent avoir des propriétaires, des montants et des ressources avant d'être validées")
        }
        if (!isValid0) {
            owner0.askValidation(owner1!!, amount0!!, ressource0!!, amount1!!, ressource1!!) { isValid0 = it }
        }
        if (!isValid1) {
            owner1!!.askValidation(owner0, amount1!!, ressource1!!, amount0!!, ressource0!!) { isValid1 = it }
        }
    }
}