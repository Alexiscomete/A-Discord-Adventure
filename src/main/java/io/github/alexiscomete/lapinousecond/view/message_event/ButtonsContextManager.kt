package io.github.alexiscomete.lapinousecond.view.message_event

import org.javacord.api.event.interaction.ButtonClickEvent

class ButtonsContextManager(val hash: HashMap<String, (ButtonClickEvent) -> Unit >) : ContextManager {

}