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
    TR_END("tr_end");

    private final String name;

    AnswerEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static final AnswerManager answerManager = new AnswerManager(AnswerEnum.class.getResourceAsStream("answers.json"));

    public static AnswerManager getAnswerManager() {
        return answerManager;
    }
}
