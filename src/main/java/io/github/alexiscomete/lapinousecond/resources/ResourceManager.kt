package io.github.alexiscomete.lapinousecond.resources

class ResourceManager(val resource: Resource, var quantity: Int) {

    override fun toString(): String {
        // pour sauvegarder dans la base de données
        return resource.progName + ":" + quantity
    }

    companion object {
        fun stringToArray(str: String?): HashMap<Resource, ResourceManager> {
            // la futur liste de toutes les ressources du joueur
            val resourceManagers = HashMap<Resource, ResourceManager>()
            if (str == null || str == "") {
                return resourceManagers
            }
            // pn sépare chaque ressource
            val strings = str.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            // comme vu précédemment le nom est suivi de : et de la quabtité
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
