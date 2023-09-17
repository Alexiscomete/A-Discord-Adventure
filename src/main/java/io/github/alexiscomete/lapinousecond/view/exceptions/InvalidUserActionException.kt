package io.github.alexiscomete.lapinousecond.view.exceptions

class InvalidUserActionException(private val baseMessage: String) : BaseCustomException() {
    override val message: String
        get() = "Merci de lire ce message en entier.\nUn problème est survenu ! L'action que vous essayez actuellement de faire est invalide, essayez plus tard dans le jeu :\n$baseMessage\nCode erreur : `E2` sur le wiki."
}
