package io.github.alexiscomete.lapinousecond.resources

class ResourceManager(val resource: Resource, var quantity: Int) {

    override fun toString(): String {
        return resource.progName + ":" + quantity
    }

    companion object {
        fun stringToArray(str: String?): HashMap<Resource, ResourceManager> {
            val resourceManagers = HashMap<Resource, ResourceManager>()
            if (str == null || str == "") {
                return resourceManagers
            }
            val strings = str.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in strings) {
                val elements = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (elements.size > 1) {
                    resourceManagers[Resource.valueOf(elements[0])] = ResourceManager(
                        Resource.valueOf(
                            elements[0]
                        ), elements[1].toInt()
                    )
                }
            }
            return resourceManagers
        }

        fun toString(resourceManagers: Collection<ResourceManager>): String {
            val stringBuilder = StringBuilder()
            for (re in resourceManagers) {
                stringBuilder.append(re.toString()).append(" ")
            }
            return stringBuilder.toString()
        }
    }
}