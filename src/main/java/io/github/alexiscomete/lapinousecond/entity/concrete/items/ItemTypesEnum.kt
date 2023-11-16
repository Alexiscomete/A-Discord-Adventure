package io.github.alexiscomete.lapinousecond.entity.concrete.items

import io.github.alexiscomete.lapinousecond.entity.concrete.items.items.*
import kotlin.reflect.KClass

enum class ItemTypesEnum(
    val typeInDatabase: String,
    private val cla: KClass<out Item>
) {
    NORMAL("normal", NormalItem::class),
    STRASBOURG_SAUSAGE("StrasbourgSausage", StrasbourgSausage::class),
    WALL_TILE("WallTileItem", WallTileItem::class),
    COMPUTER("ComputerItem", ComputerItem::class),
    BASE_SWORD("BaseSwordItem", BaseSwordItem::class),
    BASE_SHIELD("BaseShieldItem", BaseShieldItem::class),
    ;

    companion object {
        fun fromTypeInDatabase(typeInDatabase: String): ItemTypesEnum {
            return entries.firstOrNull { it.typeInDatabase == typeInDatabase }
                ?: throw IllegalStateException("Unknown item type")
        }

        fun instanciateWithData(id: Long): Item {
            // get the data
            val data = ItemData[id]
            // get the type
            val type = fromTypeInDatabase(data["type"])
            // instanciate the item
            return type.instanciateWithData(id, data)
        }
    }

    fun instanciateWithData(id: Long, data: ItemData): Item {
        // instanciate the item
        // Constructor : (id: Long, str1: String, str2: String, ...), the string value is the value in the database
        val cons = cla.constructors
        // find the first constructor that match the pattern, foreach test
        val constructor = cons.firstOrNull {
            // get the parameters
            val params = it.parameters
            // check if the first parameter is a Long
            if (params[0].type.classifier != Long::class) {
                return@firstOrNull false
            }
            // for every other parameter, check if the type is a String
            for (i in 1 until params.size) {
                if (params[i].type.classifier != String::class) {
                    return@firstOrNull false
                }
            }
            // if all the parameters are correct, return true
            return@firstOrNull true
        } ?: throw IllegalStateException("No constructor found for $typeInDatabase")
        // create the parameters
        val params = mutableListOf<Any>()
        params.add(id)
        for (i in 1 until constructor.parameters.size) {
            val name = constructor.parameters[i].name
            if (name == null) {
                params.add("")
            } else {
                params.add(data[name])
            }
        }
        // return the item
        return constructor.call(*params.toTypedArray())
    }

    fun instanciateWithData(id: Long): Item {
        return instanciateWithData(id, ItemData[id])
    }
}
