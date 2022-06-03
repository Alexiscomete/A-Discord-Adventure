package io.github.alexiscomete.lapinousecond;

/**
 * Permissions globales sur le bot
 */
public class UserPerms {

    public final boolean PLAY, CREATE_SERVER, MANAGE_PERMS, MANAGE_ROLES, isDefault;

    public UserPerms(boolean PLAY, boolean CREATE_SERVER, boolean MANAGE_PERMS, boolean MANAGE_ROLES, boolean isDefault) {
        this.PLAY = PLAY;
        this.CREATE_SERVER = CREATE_SERVER;
        this.MANAGE_PERMS = MANAGE_PERMS;
        this.isDefault = isDefault;
        this.MANAGE_ROLES = MANAGE_ROLES;
    }

    /**
     * Vérification des permissions de l'utilisateur
     * @param id id de l'utilisateur
     * @param perms permissions à vérifier
     * @return si toute il a toutes le perms true sinon false
     */
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
            if (perm.equals("MANAGE_ROLES") && !up.MANAGE_ROLES) {
                return false;
            }
        }
        return true;
    }
}
