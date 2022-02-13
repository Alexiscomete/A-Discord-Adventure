package io.github.alexiscomete.lapinousecond.entity;

import io.github.alexiscomete.lapinousecond.Item;
import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.resources.Resource;
import io.github.alexiscomete.lapinousecond.resources.ResourceManager;
import io.github.alexiscomete.lapinousecond.roles.Role;
import io.github.alexiscomete.lapinousecond.roles.RolesEnum;
import io.github.alexiscomete.lapinousecond.save.CacheGetSet;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import io.github.alexiscomete.lapinousecond.save.Tables;
import io.github.alexiscomete.lapinousecond.view.AnswerEnum;
import io.github.alexiscomete.lapinousecond.view.LangageEnum;

import java.util.ArrayList;
import java.util.HashMap;

public class Player extends CacheGetSet implements Owner {

    private double bal;
    private long server;
    private short tuto;
    private boolean isVerify;
    private boolean hasAccount;
    private int x;
    private int y;
    private long workTime;
    private final ArrayList<Role> roles;
    private final ArrayList<Item> items = new ArrayList<>();
    private final HashMap<Resource, ResourceManager> resourceManagers;
    private final SaveManager saveManager = Main.getSaveManager();

    public long getWorkTime() {
        return workTime;
    }

    public void updateWorkTime() {
        this.workTime = System.currentTimeMillis();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void updateItems() {
        StringBuilder itemsList = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            itemsList.append(item.jname);
            if (i != items.size()-1) {
                itemsList.append(";");
            }
        }
        saveManager.setValue(Tables.PLAYERS.getTable(), this.id, "items", itemsList.toString());
    }

    public double getBal() {
        return bal;
    }

    public void setBal(double bal) {
        this.bal = bal;
        saveManager.setValue(Tables.PLAYERS.getTable(), id, "bal", String.valueOf(bal));
    }

    public long getServer() {
        return server;
    }

    public void setServer(long server) {
        this.server = server;
        saveManager.setValue(Tables.PLAYERS.getTable(), id, "serv", String.valueOf(server));
    }

    public short getTuto() {
        return tuto;
    }

    public void setTuto(short tuto) {
        this.tuto = tuto;
        saveManager.setValue(Tables.PLAYERS.getTable(), id, "tuto", String.valueOf(tuto));
    }

    public boolean isVerify() {
        return isVerify;
    }

    public boolean hasAccount() {
        return hasAccount;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
        saveManager.setValue(Tables.PLAYERS.getTable(), id, "is_verify", SaveManager.toBooleanString(verify));
    }

    public void setHasAccount(boolean hasAccount) {
        this.hasAccount = hasAccount;
        saveManager.setValue(Tables.PLAYERS.getTable(), id, "has_account", SaveManager.toBooleanString(hasAccount));
    }

    public void setX(int x) {
        this.x = x;
        saveManager.setValue(Tables.PLAYERS.getTable(), id, "x", String.valueOf(x));
    }

    public void setY(int y) {
        this.y = y;
        saveManager.setValue(Tables.PLAYERS.getTable(), id, "y", String.valueOf(y));
    }

    public Player(long id, double bal, long server, short tuto, boolean isVerify, boolean hasAccount, int x, int y, String roles, String resources) {
        super(id, Tables.PLAYERS.getTable());
        this.bal = bal;
        this.server = server;
        this.tuto = tuto;
        this.isVerify = isVerify;
        this.hasAccount = hasAccount;
        this.x = x;
        this.y = y;
        this.workTime = 0;
        this.roles = RolesEnum.getRoles(roles);
        this.resourceManagers = ResourceManager.stringToArray(resources);
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
        saveManager.setValue(Tables.PLAYERS.getTable(), id, "roles", RolesEnum.rolesToString(roles));
    }

    public HashMap<Resource, ResourceManager> getResourceManagers() {
        return resourceManagers;
    }

    public void updateResources() {
        saveManager.setValue(Tables.PLAYERS.getTable(), id, "resources", ResourceManager.toString(resourceManagers.values()));
    }

    @Override
    public String getOwnerType() {
        return "player";
    }

    @Override
    public String getOwnerString() {
        return String.valueOf(id);
    }

    public String getAnswer(AnswerEnum answerEnum, Object... format) {
        String langage = getString("langage");
        LangageEnum langageEnum;
        if (langage == null || langage.equals("")) {
            langageEnum = LangageEnum.FRENCH;
        } else {
            try {
                langageEnum = LangageEnum.valueOf(langage);
            } catch (IllegalArgumentException argumentException) {
                langageEnum = LangageEnum.FRENCH;
            }
        }
        String answer = AnswerEnum.getAnswerManager().getAnswer(langageEnum, answerEnum);
        return AnswerEnum.getAnswerManager().formatAnswer(answer, format);
    }
}
