package io.github.alexiscomete.lapinousecond.data

import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.data.managesave.Table
import io.github.alexiscomete.lapinousecond.data.managesave.toBoolean

val PERMS = Table("perms")

/**
 * Permissions globales sur le bot
 */
class UserPerms(
    id: Long
) : CacheGetSet(id, PERMS) {

    var isDefault: Boolean = true

    //les permissions disponibles 
    var play: Boolean =
        if (this["play"] != "") {
            isDefault = false
            toBoolean(this["play"].toInt())
        } else true
    var createServer: Boolean =
        if (this["create_server"] != "") {
            isDefault = false
            toBoolean(this["create_server"].toInt())
        } else true
    var managePerms: Boolean =
        if (this["manage_perms"] != "") {
            isDefault = false
            toBoolean(this["manage_perms"].toInt())
        } else false
    var manageRoles: Boolean =
        if (this["manage_roles"] != "") {
            isDefault = false
            toBoolean(this["manage_roles"].toInt())
        } else false
}

/**
 * Vérification des permissions de l'utilisateur
 * @param id id de l'utilisateur
 * @param perms permissions à vérifier
 * @return si toute il a toutes le perms true sinon false
 */
fun check(id: Long, perms: Array<String>): Boolean {
    if (id == 602034791164149810L) { // owner : a tout les droits globaux
        return true
    }
    val up = UserPerms(id)
    for (perm in perms) { // les perm demandées, si une seule ne passe pas, on annule
        if (perm == "PLAY" && !up.play) return false
        if (perm == "CREATE_SERVER" && !up.createServer) {
            return false
        }
        if (perm == "MANAGE_PERMS" && !up.managePerms) {
            return false
        }
        if (perm == "MANAGE_ROLES" && !up.manageRoles) {
            return false
        }
    }
    return true
}
