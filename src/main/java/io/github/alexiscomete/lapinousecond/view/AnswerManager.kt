package io.github.alexiscomete.lapinousecond.view

import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

class AnswerManager(input: InputStream?) {
    private var jsonObject: JSONObject? = null

    init {
        if (input != null) {
            val sc = Scanner(input, StandardCharsets.UTF_8)
            val builder = StringBuilder()
            sc.forEachRemaining { s: String? -> builder.append(s).append(" ") }
            println(builder)
            jsonObject = JSONObject(builder.toString())
            sc.close()
        } else {
            jsonObject = JSONObject("{}")
        }
    }

    fun getAnswer(langageEnum: LangageEnum, answerEnum: AnswerEnum): String {
        val `object` = jsonObject!!.getJSONObject(answerEnum.value) ?: return langageEnum.invalidAnswer
        val answer = `object`.getString(langageEnum.name_)
        if (answer == null || answer == "") {
            return langageEnum.invalidAnswer
        }
        println(answer)
        return answer
    }

    fun formatAnswer(answer: String, vararg format: Any): String {
        var answer1 = answer
        var i = 1
        for (form in format) {
            answer1 = answer1.replace("replace$i", form.toString())
            i++
        }
        return answer1
    }
}