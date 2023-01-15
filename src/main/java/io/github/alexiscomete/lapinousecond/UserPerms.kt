package io.github.alexiscomete.lapinousecond

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table
import io.github.alexiscomete.lapinousecond.useful.managesave.toBoolean

val PERMS = Table("perms")

/**
 * Permissions globales sur le bot
 */
class UserPerms(
    id: Long
) : CacheGetSet(id, PERMS) {

    //les permissions disponibles 
    var play: Boolean = true
    var createServer: Boolean = true
    var managePerms: Boolean = false
    var manageRoles: Boolean = false
    var isDefault: Boolean = true

    //on regarde les permissions grâce au CacheGetSet
    init {
        if (this["play"] != "") {
            play = toBoolean(this["play"].toInt())
            isDefault = false
        }
        if (this["create_server"] != "") {
            createServer = toBoolean(this["create_server"].toInt())
            isDefault = false
        }
        if (this["manage_perms"] != "") {
            managePerms = toBoolean(this["manage_perms"].toInt())
            isDefault = false
        }
        if (this["manage_roles"] != "") {
            manageRoles = toBoolean(this["manage_roles"].toInt())
            isDefault = false
        }
    }

    companion object {
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
            for (perm in perms) { // les perm demandées, si une seule ne passe pas on annule
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
    }
}
