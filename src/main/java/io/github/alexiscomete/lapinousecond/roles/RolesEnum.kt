package io.github.alexiscomete.lapinousecond.roles

import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import java.util.*

enum class RolesEnum(
    val name_: String, val description: String, // les alias
    private val aliases: Array<String>, val salary: Int, // en secondes
    val coolDownSize: Int
) {
    ADMIN(
        "admin",
        "Administrateur du serveur Discord",
        arrayOf("admin", "orgna", "orga"),
        80,  /* 5 heures */
        43200
    ),  // pas besoin de mettre le nom complet du role car le contain ignore les espaces
    MODO("modo", "Modérateur du serveur Discord", arrayOf("modo", "modé"), 60, 43200), MEMBER(
        "member",
        "Membre du serveur Discord",
        arrayOf("memb"),
        10,  /* 1h */
        3600
    ),
    PARTICIPANT(
        "participant",
        "Participant du serveur Discord",
        arrayOf("part", "commu"),
        50,  /* 3 heures */
        10800
    ),
    VISITOR("visitor", "Visiteur du serveur Discord", arrayOf("visit"), 5,  /* 1h */3600), CITOYEN(
        "citoyen",
        "Citoyen du Dibistan / de la région / du département",
        arrayOf("citoy"),
        10,  /* 3 heures */
        10800
    ),
    AMBASSADOR("ambassadeur", "Ambassadeur", arrayOf("ambassad"), 10,  /* 3 heures */10800), DELEGATE(
        "delegate",
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
            for (role in values()) {
                if (role.check(user, server)) {
                    roles.add(role)
                }
            }
            return roles
        }
    }
}