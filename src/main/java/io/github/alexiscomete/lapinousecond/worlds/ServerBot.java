package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.save.CacheGetSet;
import io.github.alexiscomete.lapinousecond.save.Tables;

/**
 * Représente un serveur discord dans la base de données
 */
public class ServerBot extends CacheGetSet {

    /**
     * Créer un nouveau serveur / ! \ passer par SaveManager
     * @param id l'identifiant discord du serveur
     */
    public ServerBot(long id) {
        super(id, Tables.SERVERS.getTable());
    }

    /**
     * Permet de tester si le paramètre est valid en nombre de caractères puis de le régler
     * @param len longueur du message
     * @param message la valeur du paramètre
     * @param prog_name le paramètre
     * @return un message d'erreur s'il y a trop de caractères, il faudrait recoder cette partie
     */
    public String testValueAndSet(int len, String message, String prog_name) {
        if (message.length() < len) {
            set(prog_name, message);
            return "";
        } else {
            return "Impossible : trop de caractères : " + message.length() + ", nombre autorisé : " + len;
        }
    }
}
