package io.github.alexiscomete.lapinousecond.entity.roles

import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import java.util.*

enum class RolesEnum(
    val displayName: String, val description: String, // les alias
    private val aliases: Array<String>, val salary: Int, // en secondes
    val coolDownSize: Int
) {
    ADMIN(
        "Admin",
        "Administrateur du serveur Discord",
        arrayOf("admin", "orgna", "orga"),
        80,  /* 5 heures */
        43200
    ),  // pas besoin de mettre le nom complet du role car le contain ignore les espaces
    MODO(
        "Modérateur",
        "Modérateur du serveur Discord (ou un chanceux qui a un rôle qui bug)",
        arrayOf("modo", "modé"),
        60,
        43200
    ),
    MEMBER(
        "Membre",
        "Membre du serveur Discord",
        arrayOf("memb"),
        10,  /* 1h */
        3600
    ),
    PARTICIPANT(
        "Participant",
        "Participant du serveur Discord",
        arrayOf("part", "commu"),
        50,  /* 3 heures */
        10800
    ),
    VISITOR(
        "Visiteur",
        "Visiteur du serveur Discord, de passage ici puis ailleurs, et enfin là-bas",
        arrayOf("visit"),
        5,  /* 1h */
        3600
    ),
    CITOYEN(
        "Citoyen",
        "Citoyen de la ville du serveur",
        arrayOf("citoy"),
        10,  /* 3 heures */
        10800
    ),
    AMBASSADOR(
        "Ambassadeur",
        "Ambassadeur d'un autre serveur",
        arrayOf("ambassad"),
        10,  /* 3 heures */
        10800
    ),
    DELEGATE(
        "Délégué",
        "Représentant",
        arrayOf("delag", "repr"),
        10,  /* 3 heures */
        10800
    );

    fun check(user: User, server: Server?): Boolean {
        val roles = user.getRoles(server)
        for (alias in aliases) {
            for (role in roles) {
                if (role.name.lowercase(Locale.getDefault()).contains(alias)) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        fun check(strings: Array<String>, user: User, server: Server): Boolean {
            for (string in strings) {
                return when (string) {
                    "MANAGE_ADMIN" -> server.owner.get() == user
                    "MANAGE_ROLES_SERVER" -> ADMIN.check(user, server)
                    else -> {
                        println("WARNING : $string n'est pas une permission")
                        true
                    }
                }
            }
            return true
        }

        fun getRoles(user: User, server: Server?): ArrayList<RolesEnum> {
            val roles = ArrayList<RolesEnum>()
            for (role in entries) {
                if (role.check(user, server)) {
                    roles.add(role)
                }
            }
            return roles
        }
    }
}
