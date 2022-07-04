package useful.transactions

import entity.Owner
import resources.Resource

fun giveFromTo(from: Owner, to: Owner, amount: Double, resource: Resource) : Boolean {
    return if (from.hasResource(resource, amount)) {
        from.removeResource(resource, amount)
        to.addResource(resource, amount)
        true
    } else {
        false
    }
}

fun transactionTwoDirections(owner0: Owner, owner1: Owner, amount0: Double, ressource0: Resource, amount1: Double, ressource1: Resource) : Boolean {
    return if (owner0.hasResource(ressource0, amount0) && owner1.hasResource(ressource1, amount1)) {
        owner0.removeResource(ressource0, amount0)
        owner1.removeResource(ressource1, amount1)
        owner0.addResource(ressource0, amount1)
        owner1.addResource(ressource1, amount0)
        true
    } else {
        false
    }
}