package io.github.alexiscomete.lapinousecond.view.ui.playerui

class Question(
    val name: String,
    val field0: QuestionField,
    val field1: QuestionField? = null,
    val field2: QuestionField? = null,
    val field3: QuestionField? = null,
    val doAfter: () -> Question?
)