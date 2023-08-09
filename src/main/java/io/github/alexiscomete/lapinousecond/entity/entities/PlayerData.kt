package io.github.alexiscomete.lapinousecond.entity.entities

import io.github.alexiscomete.lapinousecond.Beurk
import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.data.managesave.Table
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import io.github.alexiscomete.lapinousecond.view.LangageEnum
import io.github.alexiscomete.lapinousecond.view.answerManager

val PLAYERS = Table("players")

@Beurk
open class PlayerData(id: Long) : CacheGetSet(id, PLAYERS) {

    fun updateResources(resourceManagers: HashMap<Resource, ResourceManager>) {
        this["ressources"] = ResourceManager.toString(resourceManagers.values)
    }

    fun getAnswer(answerEnum: AnswerEnum, maj: Boolean, format: ArrayList<String> = ArrayList()): String {
        val langage = this["langage"]
        val langageEnum: LangageEnum = if (langage == "") {
            LangageEnum.FRENCH
        } else {
            try {
                LangageEnum.valueOf(langage.uppercase())
            } catch (argumentException: IllegalArgumentException) {
                LangageEnum.FRENCH
            }
        }
        var answer = answerManager.formatAnswer(answerManager.getAnswer(langageEnum, answerEnum), format)
        if (maj) {
            answer = answer.substring(0, 1).uppercase() + answer.substring(1)
        }
        println("answer: $answer")
        return answer
    }
}