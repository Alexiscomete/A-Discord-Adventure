package io.github.alexiscomete.lapinousecond.entity.concrete.items

enum class ItemTypesEnum(
    val typeInDatabase: String,
) {
    NORMAL("normal"),
    STRASBOURG_SAUSAGE("StrasbourgSausage"),
    WALL_TILE("WallTileItem"),
    COMPUTER("ComputerItem"),
    BASE_SWORD("BaseSwordItem"),
    BASE_SHIELD("BaseShieldItem"),
    ;
}
