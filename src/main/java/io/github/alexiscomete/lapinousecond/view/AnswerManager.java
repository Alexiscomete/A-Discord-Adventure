package io.github.alexiscomete.lapinousecond.view;

import org.json.JSONObject;

import java.util.Scanner;

public class AnswerManager {
    private JSONObject jsonObject;

    public AnswerManager(String path) {
        Scanner sc = new Scanner(path);
        StringBuilder builder = new StringBuilder();
        sc.forEachRemaining(builder::append);
        jsonObject = new JSONObject(builder.toString());
    }

    public String getAnswer(LangageEnum langageEnum, AnswerEnum answerEnum) {
        return null;
    }
}
