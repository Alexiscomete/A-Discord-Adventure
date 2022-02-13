package io.github.alexiscomete.lapinousecond.view;

public enum AnswerEnum {

    ;

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
