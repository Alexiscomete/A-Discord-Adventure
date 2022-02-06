package io.github.alexiscomete.lapinousecond.useful;

public class ProgressionBar {

    private String indicator;
    private int size;
    private String fill;
    private int fillSize;
    private String nFill;
    private int nSize;
    private double max;
    private double value;
    private int numberChars;

    public ProgressionBar(String indicator, int size, String fill, int fillSize, String nFill, int nSize, double max, double value, int numberChars) {
        this.indicator = indicator;
        this.size = size;
        this.fill = fill;
        this.fillSize = fillSize;
        this.nFill = nFill;
        this.nSize = nSize;
        this.max = max;
        this.value = value;
        this.numberChars = numberChars;
    }

    public String getBar() {
        int min = getPartMin();
        int minChars = min/fillSize;
        min = minChars * fillSize;
        int max = numberChars - min - size;
        int maxChars = (int) Math.ceil(((double)max) / ((double)nSize));
        return before(minChars) +
                indicator +
                after(maxChars);
    }

    private String before(int n) {
        return fill.repeat(n);
    }

    private String after(int n) {
        return nFill.repeat(n);
    }

    private int getPartMin() {
        return (int) ((max / value) * numberChars);
    }

    public String getIndicator() {
        return indicator;
    }

    public int getSize() {
        return size;
    }

    public String getFill() {
        return fill;
    }

    public int getFillSize() {
        return fillSize;
    }

    public String getnFill() {
        return nFill;
    }

    public int getnSize() {
        return nSize;
    }

    public double getMax() {
        return max;
    }

    public double getValue() {
        return value;
    }

    public int getNumberChars() {
        return numberChars;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public void setFillSize(int fillSize) {
        this.fillSize = fillSize;
    }

    public void setnFill(String nFill) {
        this.nFill = nFill;
    }

    public void setnSize(int nSize) {
        this.nSize = nSize;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setNumberChars(int numberChars) {
        this.numberChars = numberChars;
    }
}
