package io.github.alexiscomete.lapinousecond.entity.xp

import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item
import io.github.alexiscomete.lapinousecond.entity.concrete.items.ItemTypesEnum
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.entities.managers.PlayerOwnerManager
import kotlin.math.min

fun applyLevelRewardsTo(ownerManager: PlayerOwnerManager, levelRewards: LevelRewards) {
    // add the coin reward
    ownerManager.addMoney(levelRewards.coinReward)
    // add the ressources reward
    levelRewards.ressourcesReward.forEach {
        // DEBUG
        println("Adding ${it.second} ${it.first.show} to ${ownerManager.ownerString}")
        ownerManager.addResource(it.first, it.second.toDouble())
    }
    // add the items reward
    levelRewards.items.forEach {
        ownerManager.addItem(it)
    }
}

class LevelRewards(val level: Int) {
    val coinReward = ((min(level * level * 0.7, level * 40.0) + level * 2 + 1) * ROUND_PRECISION_D).toInt()
        .toDouble() / ROUND_PRECISION_D

    val ressourcesReward = listOf(
        Pair(Resource.BRANCH, run {
            if (level % 2 == 0) {
                level * 10 + 10
            } else {
                0
            }
        }),
        Pair(Resource.WOOD, run {
            if (level % 2 == 1) {
                level * 7 + 10
            } else {
                0
            }
        }),
        Pair(Resource.STONE, run {
            if (level % 3 == 0) {
                (level * level * 0.5).toInt() + 10
            } else {
                0
            }
        }),
        Pair(Resource.IRON, run {
            if (level % 3 == 1) {
                (level * level * 0.5).toInt() + 10
            } else {
                0
            }
        }),
        Pair(Resource.GOLD, run {
            if (level % 3 == 2) {
                (level * level * 0.5).toInt() + 10
            } else {
                0
            }
        }),
        Pair(Resource.DIAMOND, run {
            if (level % 5 == 0) {
                (level * level * 0.5).toInt() + 10
            } else {
                0
            }
        }),
        Pair(Resource.COAL, run {
            if (level % 5 > 1) {
                (level * level * 0.5).toInt() + 10
            } else {
                0
            }
        }),
    )

    val items = run {
        val items = mutableListOf<Item>()
        if (level % 10 == 2) {
            items.add(Item.createItem(ItemTypesEnum.BASE_SWORD))
        } else if (level % 10 == 7) {
            items.add(Item.createItem(ItemTypesEnum.BASE_SHIELD))
        }
        if (level % 5 == 4) {
            items.add(Item.createItem(ItemTypesEnum.STRASBOURG_SAUSAGE))
        }
        items
    }

    /**
     * Return a list of strings that will be displayed in the level rewards menu
     */
    fun toStrings(): List<String> {
        // get the coin reward
        val coinReward = "${Resource.RABBIT_COIN.show} **$coinReward**"
        // get the ressources reward without the empty ones, in a list of strings
        val ressourcesReward = this.ressourcesReward.filter { it.second != 0 }.map {
            "${it.first.show} **${it.second}**"
        }
        // get the items reward
        val itemsReward = items.map { it.toString() }
        // return the list of strings
        return listOf(coinReward) + ressourcesReward + itemsReward
    }
}
