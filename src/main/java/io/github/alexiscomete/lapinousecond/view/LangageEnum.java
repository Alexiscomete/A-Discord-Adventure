package io.github.alexiscomete.lapinousecond.view;

public enum LangageEnum {

    FRENCH("Réponse invalide", "french"),
    DIBI("Arto kroleksi livalidial", "dibi");

    private final String invalidAnswer, name;


    LangageEnum(String invalidAnswer, String name) {
        this.invalidAnswer = invalidAnswer;
        this.name = name;
    }

    public String getInvalidAnswer() {
        return invalidAnswer;
    }

    public String getName() {
        return name;
    }
}
