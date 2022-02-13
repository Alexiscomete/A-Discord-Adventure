package io.github.alexiscomete.lapinousecond.view;

import org.json.JSONObject;

import java.util.Scanner;

public class AnswerManager {
    private final JSONObject jsonObject;

    public AnswerManager(String path) {
        Scanner sc = new Scanner(path);
        StringBuilder builder = new StringBuilder();
        sc.forEachRemaining(builder::append);
        jsonObject = new JSONObject(builder.toString());
    }

    public String getAnswer(LangageEnum langageEnum, AnswerEnum answerEnum) {
        JSONObject object = jsonObject.getJSONObject(answerEnum.getName());
        if (object == null) {
            return langageEnum.getInvalidAnswer();
        }
        String answer = object.getString(langageEnum.getName());
        if (answer == null || answer.equals("")) {
            return langageEnum.getInvalidAnswer();
        }
        return answer;
    }

    public String formatAnswer(String answer, Object... format) {
        int i = 0;
        for (Object form :
                format) {
            answer = answer.replace("replace" + i, form.toString());
        }
        return answer;
    }
}