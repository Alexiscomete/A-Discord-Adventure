package io.github.alexiscomete.lapinousecond.useful.managesave

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

@Synchronized
fun generateUniqueID(): Long {
    return try {
        Thread.sleep(2)
        System.currentTimeMillis()
    } catch (e: InterruptedException) {
        e.printStackTrace()
        System.currentTimeMillis()
    }
}

val pathStatic: String = File("").absolutePath

@Throws(IOException::class)
fun createPath(path: String) {
    val file = File(pathStatic + path)
    if (path.endsWith("/")) {
        println(file.mkdirs())
    } else if (!file.exists()) {
        println(file.createNewFile())
    }
}

/**
 * @param <E> type of content, think to add a toString() method in E!
</E> */
class SaveLocation<E>(private val sep: String, path: String, a: (String) -> E) {
    var content = ArrayList<E>()
    private val path: String
    private val file: File
    val a: (String) -> E

    init {
        this.path = pathStatic + path
        println("Searching file ${this.path}... ")
        file = File(pathStatic + path)
        this.a = a
        if (path.endsWith("/")) {
            println(file.mkdirs())
        } else if (!file.exists()) {
            println(file.createNewFile())
        }
    }

    fun saveAll() {
        val save = StringBuilder()
        for (e in content) {
            save.append(e.toString())
        }
        val fos: FileOutputStream
        try {
            fos = FileOutputStream(file)
            fos.write(save.toString().toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadAll() {
        val sc: Scanner
        try {
            sc = Scanner(file)
            val answer = StringBuilder()
            while (sc.hasNextLine()) {
                answer.append(sc.nextLine())
                if (sc.hasNextLine()) answer.append("\n")
            }
            val str = answer.toString().split(sep.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in str) {
                content.add(a(s))
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}