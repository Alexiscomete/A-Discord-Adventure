package io.github.alexiscomete.lapinousecond.roles;

public abstract class Role {
    private final int salary, coolDownSize;
    private final long coolDown;
    private final String name, desc;

    protected Role(int salary, int coolDownSize, String name, String desc) {
        this.salary = salary;
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
}