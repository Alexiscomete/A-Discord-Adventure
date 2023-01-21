package io.github.alexiscomete.lapinousecond.entity.items.interfaces

interface Temporary {

    /**
     * @return if the item is temporary
     */
    fun isTemporary(): Boolean

    /**
     * @return if the effect is activated
     */
    fun isActivated(): Boolean

    /**
     * Activate the temporary effect with the default duration
     */
    fun activateTemporary()

    /**
     * Deactivate the temporary effect
     */
    fun deactivate()

    /**
     * Cancel the temporary effect if it's activated and if possible
     */
    fun cancel(why: String = "Unknown reason")

    /**
     * Activate the temporary effect for the given time
     * @param duration in seconds
     */
    fun activateTemporary(duration: Int)

    /**
     * Activate the temporary effect for the given duration
     * @param duration in seconds
     * @param delay in seconds
     */
    fun activateTemporary(duration: Int, delay: Int)

    /**
     * @return the remaining time of the effect of the item
     */
    fun timeLeft(): Int
}