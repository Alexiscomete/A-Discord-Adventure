package io.github.alexiscomete.lapinousecond.view.exceptions

class UserCanSolveException(private val baseMessage: String) : BaseCustomException() {
    override val message: String
        get() = "Merci de lire ce message en entier.\nUn problème est survenu, mais pas de panique ! Avant d'essayer de contacter un admin suivez les instructions :\n$baseMessage\nSi le problème persiste, rendez-vous à la section `E1` du wiki ou contactez un administrateur."
}
