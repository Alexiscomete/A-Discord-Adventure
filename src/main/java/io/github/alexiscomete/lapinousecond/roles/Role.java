package io.github.alexiscomete.lapinousecond.roles;

public class Role {
    private final RolesEnum role;
    private long currentCooldown;

    public Role(RolesEnum role) {
        this.role = role;
        this.currentCooldown = 0;
    }

    public RolesEnum getRole() {
        return role;
    }

    public double getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(long time) {
        this.currentCooldown = time;
    }

    public boolean isReady() {
        // si le cooldown + le temps de recharge est inférieur au temps actuel
        return currentCooldown + role.coolDownSize < System.currentTimeMillis() / 1000;
    }
}
