import alexiscomete.managesave.CacheGetSet
import alexiscomete.managesave.SaveManager
import alexiscomete.managesave.Table

val PERMS = Table("perms")

/**
 * Permissions globales sur le bot
 */
class UserPerms(
    id: Long
) : CacheGetSet(id, PERMS) {

    var play: Boolean = true
    var createServer: Boolean = true
    var managePerms: Boolean = false
    var manageRoles: Boolean = false
    var isDefault: Boolean = true

    init {
        if (this["play"] != "") {
            play = SaveManager.toBoolean(this["play"].toInt())
            createServer = SaveManager.toBoolean(this["create_server"].toInt())
            managePerms = SaveManager.toBoolean(this["manage_perms"].toInt())
            manageRoles = SaveManager.toBoolean(this["manage_roles"].toInt())
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
            if (id == 602034791164149810L) {
                return true
            }
            val up = UserPerms(id)
            for (perm in perms) {
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