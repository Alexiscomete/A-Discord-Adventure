package io.github.alexiscomete.lapinousecond.roles;

public abstract class Role {
    private final int salary;
    private final long serverID;
    private final String progName;
    private final int coolDownSize;
    private long coolDown;
    private final String name, desc;

    protected Role(int salary, int coolDownSize, String name, String desc, long serverID, String progName) {
        this.salary = salary;
        this.serverID = serverID;
        this.progName = progName;
        this.coolDown = 0;
        this.coolDownSize = coolDownSize;
        this.name = name;
        this.desc = desc;
    }

    public int getSalary() {
        return salary;
    }

    public long getCoolDown() {
        return coolDown;
    }

    public int getCoolDownSize() {
        return coolDownSize;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public long getServerID() {
        return serverID;
    }

    public void updateCoolDown() {
        coolDown = System.currentTimeMillis();
    }

    public String getProgName() {
        return progName;
    }
}