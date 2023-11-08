package io.github.alexiscomete.lapinousecond.view.exceptions

class GameStateException(private val baseMessage: String) : BaseCustomException() {
    override val message: String
        get() = "Votre action est impossible ... pour le moment. Seuls d'autres joueurs, ou un changement d'état dans le jeu, peuvent rendre cette action possible :\n$baseMessage\nCode erreur : `E3` sur le wiki."
}
