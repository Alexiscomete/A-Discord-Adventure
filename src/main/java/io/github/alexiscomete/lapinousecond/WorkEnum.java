package io.github.alexiscomete.lapinousecond;

public enum WorkEnum {

    WORK1("default", "*Vous aidez quelqu'un à trouver le salon 'memes', vous gagnez rc Rabbitcoins !*", 15, 35),
    WORK2("default", "*Vous aider un bot à se rappeler de son préfix, vous gagnez rc Rabbitcoins !*", 16, 45),
    WORK3("default", "*Vous trouvez billet de train pour un autre serveur que vous rendez à son propriétaire, il vous récompense avec rc Rabbitcoins ...*", 15, 36),
    WORK4("default", "*Quelqu'un vous parle de Lapinou Premier, il vous donne rc Rabbitcoins car vous l'aider à le chercher, bien sûr personne ne le trouve.*", 15, 38),
    WORK5("default", "*Le serveur vous récompense avec rc Rabbitcoins pour avoir défendu le serveur contre Alphabet Lapin !*", 15, 55),
    WORK6("default", "*Carlos le chat virtuel est passé par là ... vous gagnez rc Rabbitcoins ...*", 15, 40),
    WORK7("default", "*Vous passez un peu de temps avec les Togerts, ils vous donnent rc Rabbitcoins*", 15, 40),
    WORK8("default", "*Vous échangez une information sur le Wumpus d'or contre un peu d'argent (rc)", 25, 70),
    WORK9("video games", "*Vous écrivez un article sur Minecraft qui vous rapporte rc Rabbitcoins ...*", 30, 80),
    WORK10("computer science", "*Vous recodez le système informatique en C++, on vous récompense avec rc Rabbitcoins", 40, 90);

    String type;
    String answer;
    int min;
    int max;

    WorkEnum(String type, String answer, int min, int max) {
        this.type = type;
        this.answer = answer;
        this.min = min;
        this.max = max;
    }
}
