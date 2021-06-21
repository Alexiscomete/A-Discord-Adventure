package io.github.alexiscomete.lapinousecond;

public enum WorkEnum {

    WORK1("default", "*Vous aidez quelqu'un à trouver le salon 'memes', vous gagnez rc Rabbitcoins !*", 5, 10),
    WORK2("default", "*Vous aider un bot à se rappeler de son préfix, vous gagnez rc Rabbitcoins !*", 6, 15),
    WORK3("default", "*Vous trouvez billet de train pour un autre serveur que vous rendez à son propriétaire, il vous récompense avec rc Rabbitcoins ...*", 3, 11),
    WORK4("default", "*Quelqu'un vous parle de Lapinou Premier, il vous donne rc Rabbitcoins car vous l'aider à le chercher, bien sûr personne ne le trouve.*", 4, 7),
    WORK5("default", "*Le serveur vous récompense avec rc Rabbitcoins pour avoir défendu le serveur contre Alphabet Lapin !*", 7, 12),
    WORK6("default", "*Carlos le chat virtuel est passé par là ... vous gagnez rc Rabbitcoins ...*", 6, 9),
    WORK7("default", "*Vous passez un peu de temps avec les Togerts, ils vous donnent rc Rabbitcoins*", 5, 12);

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
