package io.github.alexiscomete.lapinousecond.view;

import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AnswerManager {
    private final JSONObject jsonObject;

    public AnswerManager(InputStream input) {
        if (input != null) {
            Scanner sc = new Scanner(input, StandardCharsets.UTF_8);
            StringBuilder builder = new StringBuilder();
            sc.forEachRemaining((s) -> builder.append(s).append(" "));
            System.out.println(builder);
            jsonObject = new JSONObject(builder.toString());
            sc.close();
        } else {
            jsonObject = new JSONObject("{}");
        }
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
        System.out.println(answer);
        return answer;
    }

    public String formatAnswer(String answer, Object... format) {
        int i = 1;
        for (Object form :
                format) {
            answer = answer.replace("replace" + i, form.toString());
            i++;
        }
        return answer;
    }
}
