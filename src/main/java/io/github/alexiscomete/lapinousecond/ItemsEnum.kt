package io.github.alexiscomete.lapinousecond

/**
 *
 */
enum class ItemsEnum(val getItem: GetItem, val jname: String) {

    ;

    fun interface GetItem {
        fun getItem(args: Array<String?>): Item
    }

    companion object {
        fun toItems(str: String): ArrayList<Item> {
            val items = ArrayList<Item>()
            val strings = str.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in strings) {
                val a = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val args = arrayOfNulls<String>(a.size - 1)
                System.arraycopy(a, 1, args, 0, a.size - 1)
                for (itemsEnum in values()) {
                    if (itemsEnum.jname == a[0]) {
                        items.add(itemsEnum.getItem.getItem(args))
                    }
                }
            }
            return items
        }
    }
}