package io.github.alexiscomete.lapinousecond.roles

class Role(val role: RolesEnum) {
    var currentCooldown: Long = 0
    fun getCurrentCooldown(): Double {
        return currentCooldown.toDouble()
    }

    fun setCurrentCooldown(time: Long) {
        currentCooldown = time
    }

    // si le cooldown + le temps de recharge est inférieur au temps actuel
    val isReady: Boolean
        get() =// si le cooldown + le temps de recharge est inférieur au temps actuel
            currentCooldown + role.coolDownSize < System.currentTimeMillis() / 1000
}