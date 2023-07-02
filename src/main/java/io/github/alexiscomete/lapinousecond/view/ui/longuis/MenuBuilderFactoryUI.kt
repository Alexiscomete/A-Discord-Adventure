package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question
import java.awt.image.BufferedImage

class MenuBuilderFactoryUI(
    private val name: String,
    private val description: String
) {
    fun build(playerUI: PlayerUI, image: BufferedImage? = null): MenuBuilderUI {
        val menuBuilderUI = MenuBuilderUI(name, description, playerUI)
        if (image != null) {
            menuBuilderUI.setImage(image)
        }
        arrayListOfButton.forEach {
            menuBuilderUI.addButton(it.first, it.second, it.third)
        }
        arrayListOfMenu.forEach { triple ->
            val th = triple.third
            menuBuilderUI.addButton(
                triple.first, triple.second
            ) {
                it.setLongCustomUI(th.build(it))
                null
            }
        }
        return menuBuilderUI
    }

    private val arrayListOfButton = arrayListOf<Triple<String, String, (PlayerUI) -> Question?>>()
    private val arrayListOfMenu = arrayListOf<Triple<String, String, MenuBuilderFactoryUI>>()

    fun addButton(
        name: String, description: String, whenUsed: (PlayerUI) -> Question?
    ): MenuBuilderFactoryUI {
        arrayListOfButton.add(Triple(name, description, whenUsed))
        return this
    }

    fun addMenu(
        name: String, description: String, menuBuilderFactoryUI: MenuBuilderFactoryUI
    ): MenuBuilderFactoryUI {
        arrayListOfMenu.add(Triple(name, description, menuBuilderFactoryUI))
        return this
    }
}