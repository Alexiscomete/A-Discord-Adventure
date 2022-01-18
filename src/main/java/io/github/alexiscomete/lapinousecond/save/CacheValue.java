package io.github.alexiscomete.lapinousecond.save;

public class CacheValue {
    private String str;

    public CacheValue(String str) {

        this.str = str;
    }

    public String getString() {
        return str;
    }

    public void set(String str) {
        this.str = str;
    }
}
