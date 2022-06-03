package io.github.alexiscomete.lapinousecond

import io.github.alexiscomete.lapinousecond.Main.saveManager

/**
 * Permissions globales sur le bot
 */
class UserPerms(
    val PLAY: Boolean,
    val CREATE_SERVER: Boolean,
    val MANAGE_PERMS: Boolean,
    val MANAGE_ROLES: Boolean,
    val isDefault: Boolean
) {
    companion object {
        /**
         * Vérification des permissions de l'utilisateur
         * @param id id de l'utilisateur
         * @param perms permissions à vérifier
         * @return si toute il a toutes le perms true sinon false
         */
        fun check(id: Long, perms: Array<String>): Boolean {
            if (id == 602034791164149810L) {
                return true
            }
            val up = saveManager!!.getPlayerPerms(id)
            for (perm in perms) {
                if (perm == "PLAY" && !up.PLAY) return false
                if (perm == "CREATE_SERVER" && !up.CREATE_SERVER) {
                    return false
                }
                if (perm == "MANAGE_PERMS" && !up.MANAGE_PERMS) {
                    return false
                }
                if (perm == "MANAGE_ROLES" && !up.MANAGE_ROLES) {
                    return false
                }
            }
            return true
        }
    }
}