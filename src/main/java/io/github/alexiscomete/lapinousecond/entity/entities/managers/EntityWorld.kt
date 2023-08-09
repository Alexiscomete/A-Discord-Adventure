package io.github.alexiscomete.lapinousecond.entity.entities.managers

import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.map.PixelManager
import io.github.alexiscomete.lapinousecond.worlds.places

/**
 * EntityWorld is a class that represents the world of an entity. It is cached and a new one is created when worldName changes.
 */
class EntityWorld(
    val data: CacheGetSet,
    val world: WorldEnum
) {
    val place: Place?
        get() {
            val worldName = world.progName
            return when (data["place_${worldName}_type"]) {
                "place" -> {
                    val placeID = data["place_${worldName}_id"]
                    return places[placeID.toLong()]
                }

                "city" -> {
                    val cityID = data["place_${worldName}_id"]
                    return places[cityID.toLong()]
                }

                else -> {
                    null
                }
            }
        }

    fun setPath(path: ArrayList<PixelManager>, type: String, speed: Long) {
        savePath(path)
        data["place_${world}_path_type"] = type
        data["place_${world}_path_start"] = System.currentTimeMillis().toString()
        data["place_${world}_type"] = "path"
        data["place_${world}_speed"] = speed.toString()
    }

    private fun getPath(): ArrayList<PixelManager> {
        val currentPath = stringSaveToPath()
        if (currentPath.isEmpty()) {
            data["place_${world}_type"] = "coos"
            return ArrayList()
        }
        val lastPixel = currentPath[currentPath.size - 1]
        val startTime = data["place_${world}_path_start"].toLong()
        val timeForOnePixel = try {
            data["place_${world}_speed"].toLong()
        } catch (e: NumberFormatException) {
            1000L
        }
        val currentTime = System.currentTimeMillis()
        // le temps en ms pour 1 pixel est de timeForOnePixel, il faut enlever tous les pixels déjà parcourus de la liste puis la sauvegarder
        val numberOfPixel = (currentTime - startTime) / timeForOnePixel
        // les pixels à enlever sont au début de la liste, j'ai besoin que des pixels restants
        val remainingPath = ArrayList<PixelManager>()
        for (i in numberOfPixel.toInt() until currentPath.size) {
            remainingPath.add(currentPath[i])
        }
        savePath(remainingPath)
        if (remainingPath.size < 2) {
            data["place_${world}_type"] = "coos"
            data["place_${world}_x"] = lastPixel.x.toString()
            data["place_${world}_y"] = lastPixel.y.toString()
        }
        data["place_${world}_path_start"] = System.currentTimeMillis().toString()
        return remainingPath
    }

    private fun savePath(remainingPath: ArrayList<PixelManager>) {
        val pathStr = StringBuilder()
        for (pixel in remainingPath) {
            pathStr.append(pixel.x)
            pathStr.append(",")
            pathStr.append(pixel.y)
            pathStr.append(";")
        }
        data["place_${world}_path"] = pathStr.toString()
    }

    private fun stringSaveToPath(): ArrayList<PixelManager> {
        val pathStr = data["place_${world}_path"]
        val path = ArrayList<PixelManager>()
        if (pathStr != "") {
            val pathSplit = pathStr.split(";")
            for (i in pathSplit.indices) {
                if (pathSplit[i] != "") {
                    val pixelSplit = pathSplit[i].split(",")
                    val world = WorldEnum.valueOf(data["world"])
                    path.add(world.getPixel(pixelSplit[0].toInt(), pixelSplit[1].toInt()))
                }
            }
        }
        return path
    }

    inline var xStr
        inline get() = data["place_${world}_x"]
        inline set(value) {
            data["place_${world}_x"] = value
        }
    inline var yStr
        inline get() = data["place_${world}_y"]
        inline set(value) {
            data["place_${world}_y"] = value
        }

    fun positionToString(): String {
        // on récupère le type de lieu, on sépare encore en plusieurs possibilités
        return when (data["place_${world}_type"]) {
            "coos" -> {
                // on retourne le résultat
                "Vous êtes dans le monde ${world}, sur des coordonnées ($xStr, $yStr)"
            }

            "place" -> {
                val placeID = data["place_${world}_id"]
                "Vous êtes dans le monde ${world}, dans le lieu $placeID"
            }

            "path" -> {
                val path = getPath()
                if (path.isEmpty()) {
                    data["place_${world}_type"] = "coos"
                    "Vous êtes dans le monde ${world}, sur des coordonnées ($xStr, $yStr)"
                } else {
                    val firstPixel = path[0]
                    val lastPixel = path[path.size - 1]
                    "Vous êtes dans le monde ${world}, sur un chemin. Le premier pixel est (${firstPixel.x}, ${firstPixel.y}), le dernier pixel est (${lastPixel.x}, ${lastPixel.y})"
                }
            }

            "city" -> {
                val cityID = data["place_${world}_id"]
                "Vous êtes dans le monde ${world}, dans la ville $cityID"
            }

            "building" -> {
                val buildingID = data["place_${world}_building_id"]
                "Vous êtes dans le monde ${world}, dans le bâtiment $buildingID"
            }

            else -> {
                "Vous êtes dans le monde ${world}, mais vous ne savez pas où vous êtes"
            }
        }

    }
}