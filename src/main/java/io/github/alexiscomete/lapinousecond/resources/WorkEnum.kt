package io.github.alexiscomete.lapinousecond.resources;

public enum WorkEnum {

    WORK1("default", "*Vous aidez quelqu'un à trouver le salon 'memes', vous gagnez rc Rabbitcoins !*", 15, 35, null, 2),
    WORK2("default", "*Vous aider un bot à se rappeler de son préfix, vous gagnez rc Rabbitcoins !*", 16, 45, null, 2),
    WORK3("default", "*Vous trouvez billet de train pour un autre serveur que vous rendez à son propriétaire, il vous récompense avec rc Rabbitcoins ...*", 15, 36, null, 2),
    WOOD1("default", "Vous coupez du bois dans une forêt proche. Vous revenez avec rc bois", 100, 200, Resource.WOOD, 20),
    STONE1("default", "Vous allez récupérer rc pierres sur une colline", 80, 150, Resource.STONE, 20),
    BRANCH1("default", "Vous récupérez rc branches mortes dans la forêt", 50, 250, Resource.BRANCH, 15),
    BRANCH2("default", "Vous cassez rc branches sur des arbres.", 150, 250, Resource.BRANCH, 15),
    DIAMOND1("default", "En allant minez vous trouvez rc diamant(s) ...", 1, 10, Resource.DIAMOND, 1);

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

    private final String type, answer;
    private int min, max;
    private final Resource resource;
    private int coef;

    WorkEnum(String type, String answer, int min, int max, Resource resource, int coef) {
        this.type = type;
        this.answer = answer;
        this.min = min;
        this.max = max;
        this.resource = resource;
        this.coef = coef;
    }

    public int getCoef() {
        return coef;
    }

    public void setCoef(int coef) {
        this.coef = coef;
    }
}
