package io.github.alexiscomete.lapinousecond.resources;

public enum WorkEnum {

    WORK1("default", "*Vous aidez quelqu'un à trouver le salon 'memes', vous gagnez rc Rabbitcoins !*", 15, 35, 5, 100, null),
    WORK2("default", "*Vous aider un bot à se rappeler de son préfix, vous gagnez rc Rabbitcoins !*", 16, 45, 5, 100, null),
    WORK3("default", "*Vous trouvez billet de train pour un autre serveur que vous rendez à son propriétaire, il vous récompense avec rc Rabbitcoins ...*", 15, 36, 5, 100, null),
    WOOD1("default", "Vous coupez du bois dans une forêt proche. Vous revenez avec rc bois", 100, 200, 40, 100, Resource.WOOD),
    STONE1("default", "Vous allez récupérer rc pierres sur une colline", 80, 150, 40, 100, Resource.STONE),
    BRANCH1("default", "Vous récupérez rc branches mortes dans la forêt", 50, 250, 40, 100, Resource.BRANCH),
    BRANCH2("default", "Vous cassez rc branches sur des arbres.", 150, 250, 50, 100, Resource.BRANCH),
    DIAMOND1("default", "En allant minez vous trouvez rc diamant(s) ...", 1, 10, 100, 100, Resource.DIAMOND);

    public String getType() {
        return type;
    }

    public String getAnswer() {
        return answer;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Resource getResource() {
        return resource;
    }

    public int getRandomMin() {
        return randomMin;
    }

    public void setRandomMin(int randomMin) {
        this.randomMin = randomMin;
    }

    public int getRandomMax() {
        return randomMax;
    }

    public void setRandomMax(int randomMax) {
        this.randomMax = randomMax;
    }

    private final String type, answer;
    private int min, max, randomMin, randomMax;
    private final Resource resource;

    WorkEnum(String type, String answer, int min, int max, int randomMin, int randomMax, Resource resource) {
        this.type = type;
        this.answer = answer;
        this.min = min;
        this.max = max;
        this.randomMin = randomMin;
        this.randomMax = randomMax;
        this.resource = resource;
    }
}
