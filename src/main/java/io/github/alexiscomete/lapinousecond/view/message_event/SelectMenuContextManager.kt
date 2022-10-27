package io.github.alexiscomete.lapinousecond.view.message_event

import org.javacord.api.event.interaction.SelectMenuChooseEvent
import java.util.function.Consumer

class SelectMenuContextManager(val hash: HashMap<String, (SelectMenuChooseEvent) -> Unit>) : ContextManager {

}