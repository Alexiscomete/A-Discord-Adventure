package io.github.alexiscomete.lapinousecond.save

data class Table(val name: String) {
    init {
        save?.execute("CREATE TABLE IF NOT EXISTS $name\n(\nid INTEGER PRIMARY KEY\n)", false)
        save?.execute("ALTER TABLE $name ADD COLUMN id INTEGER PRIMARY KEY", false)
    }
}