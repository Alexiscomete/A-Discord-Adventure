package io.github.alexiscomete.lapinousecond.view;

public enum AnswerEnum {

    NAME("name"),
    DESCR("description"),
    PROGRESSION("progression"),
    OWNER("owner"),
    BUILDING_BA("building_ba"),
    ECHEC_TRANS("echec_trans"),
    NO_ENOUGH_MONEY("no_enough_money"),
    CONFIRMATION("confirmation"),
    MONTANT_TR("montant_tr"),
    TR_END("tr_end"),
    ASK_MONTANT("ask_montant"),
    FORM_INVALID("form_invalid"),
    VALUE_TOO_HIGH("value_too_high"),
    IMP_SIT("imp_sit"),
    NO_ENOUGH_ARGS("no_enough_args"),
    ILLEGAL_ARGUMENT_NUMBER("illegal_argument_number"),
    ENTREE_BUILD("entree_build");

    private final String name;

    AnswerEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static final AnswerManager answerManager = new AnswerManager(AnswerEnum.class.getClassLoader().getResourceAsStream("answers.json"));

    public static AnswerManager getAnswerManager() {
        return answerManager;
    }
}
