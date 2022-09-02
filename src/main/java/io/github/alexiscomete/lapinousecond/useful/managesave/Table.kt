package io.github.alexiscomete.lapinousecond.useful.managesave

data class Table(val name: String) {
    init {
        println("Table $name created")
        saveManager.execute("CREATE TABLE IF NOT EXISTS $name\n(\nid INTEGER PRIMARY KEY\n)", true)
        saveManager.execute("ALTER TABLE $name ADD COLUMN id INTEGER PRIMARY KEY", true)
        println("Table $name created")
    }
}