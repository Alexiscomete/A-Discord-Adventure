package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles

/**
 * Permet de gérer les groupes de tiles qui forment une structure. Par exemple, cela peut être un pont, un portail de téléportation, une maison, etc.
 */
interface MultiTilesManager {
    fun load()

    /**
     * Permet d'obtenir la Tile de base à la position x, y dans l'objet complex. Si la position n'est pas dans l'objet complex, une erreur est levée. Cette méthode peut-être utilisée notamment pour créer un lien entre l'extérieur de l'objet complex et l'intérieur.
     */
    fun baseTileAt(x: Int, y: Int): ComplexTile

    fun hasTileAt(x: Int, y: Int): Boolean

    /**
     * Permet de savoir si l'objet complex peut être supprimé. Si toutes les tiles de l'objet complex sont non chargées, alors l'objet complex peut être supprimé.
     */
    fun canBeRemoved(): Boolean

    /**
     * Permet de supprimer toutes les tiles de l'objet complex.
     */
    fun removeAllTiles()

    /**
     * Permet à une tile de dire à l'objet complex "Salut ! Merci de ne pas me supprimer !", ainsi l'objet complex ne sera pas supprimé après un appel à canBeRemoved().
     *
     * @see canBeRemoved
     */
    fun iAmLoaded()
}