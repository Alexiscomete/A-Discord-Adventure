package io.github.alexiscomete.lapinousecond.roles;

public abstract class Role {
    private final int salary;
    private final long coolDown;

    protected Role(int salary, long coolDown) {
        this.salary = salary;
        this.coolDown = coolDown;
    }

    public int getSalary() {
        return salary;
    }

    public long getCoolDown() {
        return coolDown;
    }
}