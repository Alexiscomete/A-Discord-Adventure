package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.save.CacheCustom
import io.github.alexiscomete.lapinousecond.save.CacheGetSet
import io.github.alexiscomete.lapinousecond.save.Table
import io.github.alexiscomete.lapinousecond.save.Tables

val SERVERS = Table("guilds")

val servers = CacheCustom(SERVERS) { id: Long -> ServerBot(id) }

/**
 * Représente un serveur discord dans la base de données
 * Créer un nouveau serveur / ! \ passer par SaveManager
 * @param id l'identifiant discord du serveur
 */
class ServerBot(id: Long) : CacheGetSet(id, SERVERS) {
    /**
     * Permet de tester si le paramètre est valid en nombre de caractères puis de le régler
     * @param len longueur du message
     * @param message la valeur du paramètre
     * @param prog_name le paramètre
     * @return un message d'erreur s'il y a trop de caractères, il faudrait recoder cette partie
     */
    fun testValueAndSet(len: Int, message: String, prog_name: String?): String {
        return if (message.length < len) {
            set(prog_name!!, message)
            ""
        } else {
            "Impossible : trop de caractères : " + message.length + ", nombre autorisé : " + len
        }
    }
}