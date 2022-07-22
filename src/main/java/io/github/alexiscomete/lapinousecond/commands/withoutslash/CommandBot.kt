package io.github.alexiscomete.lapinousecond.commands.withoutslash

import io.github.alexiscomete.lapinousecond.UserPerms
import io.github.alexiscomete.lapinousecond.commands.withoutslash.classes.Help
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import org.javacord.api.event.message.MessageCreateEvent
import io.github.alexiscomete.lapinousecond.commands.withoutslash.classes.*

fun loadAll() {
    load(Help())
    load(ConfigServ())
    load(Give())
    load(BuildingCommand())
    load(Hub())
    load(InventoryC())
    load(Invite())
    load(MapCommand())
    load(PermsManager())
    load(PlaceCommand())
    load(PlayerShop())
    load(Shop())
    load(StartAdventure())
    load(TestCommand())
    load(Travel())
    load(UseCommand())
    load(WorldCommand())
}

fun load(command: CommandBot) {
    ListenerMain.commands[command.name] = command
}

abstract class CommandBot(
    val description: String,
    val name: String,
    val totalDescription: String,
    vararg perms: String
) {
    @JvmField
    val perms: Array<out String>

    init {
        this.perms = perms
    }

    fun checkAndExecute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        if (messageCreateEvent.isServerMessage) {
            val serverOptional = messageCreateEvent.server
            if (serverOptional.isPresent) {
                val s = serverOptional.get()
                if (s.id == 904736069080186981L && messageCreateEvent.channel.id != 914268153796771950L) {
                    return
                } else {
                    val serverTextChannelOp = messageCreateEvent.serverTextChannel
                    if (serverTextChannelOp.isPresent) {
                        val sC = serverTextChannelOp.get()
                        val name = sC.name
                        // je pense que limiter les salons est important, venture permet d'inclure adventure et aventure
                        if (!(name.contains("bot") || name.contains("command") || name.contains("spam") || name.contains(
                                "🤖"
                            ) || name.contains("venture"))
                        ) {
                            return
                        }
                    }
                }
            }
        }
        try {
            if (perms.isEmpty()) {
                execute(messageCreateEvent, content, args)
                return
            }
            if (UserPerms.check(messageCreateEvent.messageAuthor.id, perms as Array<String>)) {
                execute(messageCreateEvent, content, args)
            } else {
                messageCreateEvent.message.reply("Vous n'avez pas le droit d'exécuter cette commande")
            }
        } catch (e: Exception) {
            messageCreateEvent.message.reply(
                """
    Erreur : 
    ```
    ${e.localizedMessage}
    ```
    """.trimIndent()
            )
            e.printStackTrace()
        }
    }

    abstract fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>)

    fun sendImpossible(messageCreateEvent: MessageCreateEvent, p: Player) {
        messageCreateEvent.message.reply(p.getAnswer(AnswerEnum.IMP_SIT, true))
    }

    fun sendArgs(messageCreateEvent: MessageCreateEvent, p: Player) {
        messageCreateEvent.message.reply(p.getAnswer(AnswerEnum.NO_ENOUGH_ARGS, true))
    }

    fun sendNumberEx(messageCreateEvent: MessageCreateEvent, p: Player, i: Int) {
        messageCreateEvent.message.reply(p.getAnswer(AnswerEnum.ILLEGAL_ARGUMENT_NUMBER, true, arrayListOf(i.toString())))
    }

    fun isNotNumeric(str: String): Boolean {
        return try {
            str.toInt()
            false
        } catch (e: NumberFormatException) {
            true
        }
    }
}