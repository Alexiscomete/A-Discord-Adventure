package io.github.alexiscomete.lapinousecond;

public class Territory {
    public int getValue(long time, int numberOfBattles) {
        return getValueTime(time) * getValueBattles(numberOfBattles);
    }

    public int getValueTime(long time) {
        long minutes = time / 60000;
        if (minutes > 5) {
            if (minutes > 15) {
                if (minutes > 40) {
                    if (minutes > 60) {
                        if (minutes > 90) {
                            if (minutes > 120) {
                                if (minutes > 200) {
                                    if (minutes > 260) {
                                        if (minutes > 360) {
                                            if (minutes > 460) {
                                                if (minutes > 560) {
                                                    if (minutes > 660) {
                                                        if (minutes > 800) {
                                                            if (minutes > 1000) {
                                                                if (minutes > 2000) {
                                                                    if (minutes > 3000) {
                                                                        if (minutes > 4000) {
                                                                            if (minutes > 5000) {
                                                                                return (int) minutes;
                                                                            }
                                                                            return 1000;
                                                                        }
                                                                        return 600;
                                                                    }
                                                                    return 400;
                                                                }
                                                                return 200;
                                                            }
                                                            return 100;
                                                        }
                                                        return 80;
                                                    }
                                                    return 69;
                                                }
                                                return 56;
                                            }
                                            return 44;
                                        }
                                        return 35;
                                    }
                                    return 27;
                                }
                                return 22;
                            }
                            return 15;
                        }
                        return 10;
                    }
                    return 7;
                }
                return 5;
            }
            return 3;
        }
        return 1;
    }

    public int getValueBattles(int numberOfBattles) {
        switch (numberOfBattles) {
            case 0:
                return 1;
            case 1:
                return 20;
            case 2:
                return 35;
            case 3:
                return 50;
            case 4:
                return 75;
            case 5:
                return 100;
            case 6:
                return 150;
            case 7:
                return 210;
            case 8:
                return 280;
            case 9:
                return 400;
            case 10:
                return 700;
            default:
                return 800 + (90 * (numberOfBattles - 8));
        }
    }
}
