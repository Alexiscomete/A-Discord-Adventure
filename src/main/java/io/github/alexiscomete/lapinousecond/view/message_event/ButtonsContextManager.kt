package io.github.alexiscomete.lapinousecond.view.message_event

import io.github.alexiscomete.lapinousecond.view.Context
import org.javacord.api.event.interaction.ButtonClickEvent

class ButtonsContextManager(val hash: HashMap<String, (ButtonClickEvent, Context, ButtonsContextManager) -> Unit >) : ContextManager {

}