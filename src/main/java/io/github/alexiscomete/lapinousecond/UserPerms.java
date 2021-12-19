package io.github.alexiscomete.lapinousecond;

public class UserPerms {

    public final boolean PLAY, CREATE_SERVER, SET_SERVER_SEC, MANAGE_PERMS, isDefault;

    public UserPerms(boolean PLAY, boolean CREATE_SERVER, boolean SET_SERVER_SEC, boolean MANAGE_PERMS, boolean isDefault) {
        this.PLAY = PLAY;
        this.CREATE_SERVER = CREATE_SERVER;
        this.SET_SERVER_SEC = SET_SERVER_SEC;
        this.MANAGE_PERMS = MANAGE_PERMS;
        this.isDefault = isDefault;
    }

    public static boolean check(long id, String[] perms) {
        if (id == 602034791164149810L) {
            return true;
        }
        UserPerms up = Main.getSaveManager().getPlayerPerms(id);
        for (String perm : perms) {
            if (perm.equals("PLAY") && !up.PLAY) return false;
            if (perm.equals("CREATE_SERVER") && !up.CREATE_SERVER) {
                return false;
            }
            if (perm.equals("SET_SERVER_SEC") && !up.SET_SERVER_SEC) {
                return false;
            }
            if (perm.equals("MANAGE_PERMS") && !up.MANAGE_PERMS) {
                return false;
            }
        }
        return true;
    }
}
