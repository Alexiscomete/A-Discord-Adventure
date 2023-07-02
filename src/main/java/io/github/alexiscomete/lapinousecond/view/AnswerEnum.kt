package io.github.alexiscomete.lapinousecond.view

val answerManager = AnswerManager(AnswerEnum::class.java.classLoader.getResourceAsStream("answers.json"))

enum class AnswerEnum {
    NAME,
    DESCR,
    PROGRESSION,
    OWNER,
    BUILDING_BA,
    ECHEC_TRANS,
    NO_ENOUGH_MONEY,
    CONFIRMATION,
    MONTANT_TR,
    TR_END,
    ASK_MONTANT,
    FORM_INVALID,
    VALUE_TOO_HIGH,
    IMP_SIT,
    NO_ENOUGH_ARGS,
    ILLEGAL_ARGUMENT_NUMBER,
    ENTREE_BUILD,
    LIST_BUILDINGS;
}