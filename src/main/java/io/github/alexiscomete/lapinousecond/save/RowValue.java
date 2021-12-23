package io.github.alexiscomete.lapinousecond.save;

public abstract class RowValue {
    private final TableRow row;

    protected RowValue(TableRow row) {
        this.row = row;
    }

    public abstract String getValue();

    public TableRow getRow() {
        return row;
    }
}
