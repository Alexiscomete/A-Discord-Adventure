package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.interaction.SlashCommandOption

/**
 * contenue dans une commande, utilisée dans les classes abstraites de groupes et de sous commandes uniquement  
*/
interface Sub {
    /**
     * Le nom de cette sous commande, utilisée uniquement côté Discord
    */   
    val name: String
    /**
     * de même que le nom, utilisé pour envoyer à Discord 
    */
    val description: String

    /**
     * obtenir les informations complètes à envoyer à Discord , nom et description utilisés ici.
    */
    fun getS(): SlashCommandOption
}
