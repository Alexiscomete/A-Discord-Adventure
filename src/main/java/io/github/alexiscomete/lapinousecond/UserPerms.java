package io.github.alexiscomete.lapinousecond;

public class UserPerms {

    public final boolean PLAY, CREATE_SERVER, MANAGE_PERMS, isDefault;

    public UserPerms(boolean PLAY, boolean CREATE_SERVER, boolean MANAGE_PERMS, boolean isDefault) {
        this.PLAY = PLAY;
        this.CREATE_SERVER = CREATE_SERVER;
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
            if (perm.equals("MANAGE_PERMS") && !up.MANAGE_PERMS) {
                return false;
            }
        }
        return true;
    }
}
