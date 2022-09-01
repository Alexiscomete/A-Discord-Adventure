package io.github.alexiscomete.lapinousecond.message_event

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent

class EmbedPagesWithInteractions<U>(
    builder: EmbedBuilder,
    uArrayList: ArrayList<U>,
    uAddContent: AddContent<U>,
    whenSelected: (U, ButtonClickEvent) -> Unit
) : EmbedPages<U>(
    builder,
    uArrayList,
    uAddContent
) {
    override val number = 5
    init {
        builder.setFooter("Cliquez sur les numéros pour interagir avec un élément")
    }

}