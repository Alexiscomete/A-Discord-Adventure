package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.data.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question
import java.awt.image.BufferedImage

class MenuBuilderUI(private val name: String, private val description: String, playerUI: PlayerUI) : StaticUI(listOf(), playerUI) {

    private var bufferedImage: BufferedImage? = null

    private val arrayListOfButton = arrayListOf<Pair<String, String>>()

    fun addButton(
        name: String, description: String, whenUsed: (PlayerUI) -> Question?
    ): MenuBuilderUI {

        val id = generateUniqueID()

        addInteractionUICustomUI(
            SimpleInteractionUICustomUI(
                id.toString(), name, description, InteractionStyle.NORMAL, whenUsed, null
            )
        )

        arrayListOfButton.add(Pair(name, description))

        return this
    }

    fun setImage(image: BufferedImage): MenuBuilderUI {
        bufferedImage = image
        return this
    }

    override fun getTitle(): String {
        return name
    }

    override fun getDescription(): String {
        return description
    }

    override fun getFields(): List<Pair<String, String>> {
        return arrayListOfButton
    }

    override fun getLinkedImage(): String? {
        return null
    }

    override fun getBufferedImage(): BufferedImage? {
        return bufferedImage
    }

    override fun getUnderString(): String {
        return "Menu généré automatiquement"
    }
}