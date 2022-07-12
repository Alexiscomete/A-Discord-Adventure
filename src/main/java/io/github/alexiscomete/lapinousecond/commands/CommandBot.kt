package io.github.alexiscomete.lapinousecond.commands

import io.github.alexiscomete.lapinousecond.UserPerms
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import org.javacord.api.event.message.MessageCreateEvent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import io.github.alexiscomete.lapinousecond.commands.*


/*fun findAllClassesUsingClassLoader(packageName: String) {
    println(packageName.replace("[.]".toRegex(), "/"))
    val stream = CommandBot::class
        .java
        .classLoader
        .getResourceAsStream(
            packageName.replace("[.]".toRegex(), "/"))
    val reader =
        BufferedReader(
            InputStreamReader(
                stream!!))
    reader.lines()
        .filter { line: String -> line.endsWith(".class") }
        .map { line: String ->
            getClass(
                line,
                packageName
            )
        }
}

@Throws(IOException::class)
fun loadClasses(basePackage: String) {
    var basePackage1 = basePackage
    val jar: JarFile = JarFile(moduleJar)
    basePackage1 = basePackage1.replace('.', '/') + "/"
    try {
        for (e in jar.entries()) {
            if (e.name.startsWith(basePackage1) && e.name.endsWith(".class")) {
                val c: String = e.name.replace('/', '.').substring(0, e.name.length - ".class".length)
                try {
                    val urls: Array<URL> = arrayOf<URL>(URL("jar:file:" + moduleJar.getAbsolutePath() + "!/"))
                    val loader = URLClassLoader(urls, CommandBot::class.java.classLoader)
                    val clazz = Class.forName(c, true, loader)
                    clazz.getDeclaredMethod("load").invoke(null)
                } catch (ignored: InvocationTargetException) {
                } catch (ignored: IllegalAccessException) {
                } catch (ignored: NoSuchMethodException) {
                }
            }
        }
    } finally {
        try {
            jar.close()
        } catch (ignored: IOException) {
        }
    }
}

private fun getClass(className: String, packageName: String): Class<*>? {
    try {
        return Class.forName(
            packageName + "."
                    + className.substring(0, className.lastIndexOf('.'))
        )
    } catch (e: ClassNotFoundException) {
        // handle the exception
    }
    return null
}*/

fun load(command: CommandBot): CommandBot {
    ListenerMain.commands[command.name] = command
    return command
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