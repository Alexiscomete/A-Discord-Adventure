package io.github.alexiscomete.lapinousecond.data

enum class TutoSteps(
    val number: String,
    nextStep: TutoSteps? = null,
) {
    STEP_POSITION("6"),
    STEP_SHOP("5", STEP_POSITION),
    STEP_INVENTORY_FULL("4", STEP_SHOP),
    STEP_WORK("3", STEP_INVENTORY_FULL),
    STEP_INVENTORY_EMPTY("1", STEP_WORK),
    ;

    val nextStepNum: String = nextStep?.number ?: "-1"
}