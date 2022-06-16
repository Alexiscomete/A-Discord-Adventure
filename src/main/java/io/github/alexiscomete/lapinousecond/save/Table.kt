package io.github.alexiscomete.lapinousecond.save

data class Table(val name: String) {
    init {
        saveManager?.execute("CREATE TABLE IF NOT EXISTS $name\n(\nid INTEGER PRIMARY KEY\n)", false)
        saveManager?.execute("ALTER TABLE $name ADD COLUMN id INTEGER PRIMARY KEY", false)
    }
}