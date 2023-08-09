package io.github.alexiscomete.lapinousecond.entity.entities.managers

import io.github.alexiscomete.lapinousecond.entity.roles.Role

class WorkManager {
    var workTime: Long = 0
        private set
    val roles: ArrayList<Role> = ArrayList()

    fun updateWorkTime() {
        workTime = System.currentTimeMillis()
    }
}