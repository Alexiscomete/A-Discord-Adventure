package io.github.alexiscomete.lapinousecond.save;

public class TableRow {
    private final String name, type;

    public TableRow(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
