package aoc2022

import readInput
import kotlin.math.max

private enum class Material { ORE, CLAY, OBSIDIAN, GEODE }
private typealias Robot = Material

private data class BuildCost(val type: Material, val amount: Int)

private data class Blueprint(val id: Int, val costs: Map<Robot, List<BuildCost>>) {
    companion object {
        fun fromString(input: String): Blueprint {
            // Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 20 clay. Each geode robot costs 2 ore and 12 obsidian.
            val split = input.drop("Blueprint ".length).split(":")
            val id = split[0].toInt()
            val costs = mutableMapOf<Robot, List<BuildCost>>()
            split[1].split(".").filter { it.isNotBlank() }.map { recipe ->
                val type = Robot.valueOf(recipe.drop(" Each ".length).takeWhile { it != ' ' }.uppercase())
                val cost = recipe.dropWhile { !it.isDigit() }.split(" and ").map {
                    val (amount, material) = it.split(" ")
                    BuildCost(Material.valueOf(material.uppercase()), amount.toInt())
                }.toList()
                costs[type] = cost
            }
            return Blueprint(id, costs)
        }
    }

    override fun toString() = id.toString()

    val maxCost by lazy {
        intArrayOf(
            costs.values.maxOf { buildCosts -> buildCosts.find { it.type == Material.ORE }?.amount ?: 0 },
            costs.values.maxOf { buildCosts -> buildCosts.find { it.type == Material.CLAY }?.amount ?: 0 },
            costs.values.maxOf { buildCosts -> buildCosts.find { it.type == Material.OBSIDIAN }?.amount ?: 0 },
            Int.MAX_VALUE
        )
    }
    val obsidianForGeode = costs[Material.GEODE]!!.find { it.type == Material.OBSIDIAN }!!.amount
    val clayForObsidian = costs[Material.OBSIDIAN]!!.find { it.type == Material.CLAY }!!.amount
}

private class State(
    val blueprint: Blueprint,
    val timePassed: Int,
    val remainingTime: Int,
    val inventory: IntArray,
    val robots: IntArray
) {
    fun canBuild(robot: Robot) =
        blueprint.costs[robot]!!.all { (material, amount) -> inventory[material.ordinal] >= amount }

    fun shouldBuild(robot: Robot): Boolean {
        if (robot == Robot.GEODE) return true
        // false, if we already produce more than the most expensive robot costs
        return (robots[robot.ordinal] < blueprint.maxCost[robot.ordinal]) &&
                // or our inventory is already full enough to the build most expensive robot in each remaining round
                (inventory[robot.ordinal] < blueprint.maxCost[robot.ordinal] * remainingTime)
    }

    fun harvest(): State {
        val newInventory = inventory.clone()
        Robot.values().forEach { type ->
            newInventory[type.ordinal] = inventory[type.ordinal] + robots[type.ordinal]
        }
        return State(blueprint, timePassed + 1, remainingTime - 1, newInventory, robots)
    }

    fun build(robot: Robot): State {
        val newInventory = inventory.clone()
        val newRobots = robots.clone()
        blueprint.costs[robot]!!.forEach { (material, amount) ->
            newInventory[material.ordinal] = inventory[material.ordinal] - amount
        }
        newRobots[robot.ordinal] = robots[robot.ordinal] + 1
        return State(blueprint, timePassed, remainingTime, newInventory, newRobots)
    }

    /**
     * Upper bound on how much geode we can harvest in this state
     */
    val maxGeodePossible by lazy {
        var geodeRobots = robots[Robot.GEODE.ordinal]
        var obsidianRobots = robots[Robot.OBSIDIAN.ordinal]
        var clayRobots = robots[Robot.CLAY.ordinal]
        var geodes = inventory[Material.GEODE.ordinal]
        var obsidian = inventory[Material.OBSIDIAN.ordinal]
        var clay = inventory[Material.CLAY.ordinal]

        repeat(remainingTime) {
            // for the maximum, we can just assume that we build every possible robot in each round (ignoring ore as we
            // should be able to "build nothing" to save ore etc.)
            if (obsidian >= blueprint.obsidianForGeode) {
                geodeRobots++
                obsidianRobots++
                clayRobots++
                obsidian -= blueprint.obsidianForGeode
            } else if (clay >= blueprint.clayForObsidian) {
                obsidianRobots++
                clayRobots++
                clay -= blueprint.clayForObsidian
            } else {
                clayRobots++
            }
            geodes += geodeRobots
            obsidian += obsidianRobots
            clay += clayRobots
        }
        geodes
    }
}

private var globalBest = 0

private fun collectGeodesRecursive(state: State): Int {
    return if (state.remainingTime == 1) {
        val s = state.harvest()
        s.inventory[Material.GEODE.ordinal]
    } else if (state.maxGeodePossible < globalBest) {
        0
    } else {
        // every robot harvests its material
        val stateHarvesting = state.harvest()

        // build nothing
        var bestResult = collectGeodesRecursive(stateHarvesting)

        Robot.values().filter { state.canBuild(it) }.filter { state.shouldBuild(it) }.forEach { robot ->
            val newState = stateHarvesting.build(robot)
            val result = collectGeodesRecursive(newState)
            bestResult = max(result, bestResult)
        }

        globalBest = max(bestResult, globalBest)

        bestResult
    }
}

private fun part1(input: List<String>): Int {
    val blueprints = input.map { Blueprint.fromString(it) }
    return blueprints.sumOf { blueprint ->
        globalBest = 0
        val start = System.currentTimeMillis()
        val inventory = IntArray(Material.values().size)
        val robots = IntArray(Robot.values().size).also { it[Robot.ORE.ordinal] = 1 }
        val state = State(blueprint, 0, 24, inventory, robots)
        val geodes = collectGeodesRecursive(state)
        println("Blueprint $blueprint -> $geodes, time=${System.currentTimeMillis() - start} ms")
        geodes * blueprint.id
    }
}

private fun part2(input: List<String>): Int {
    val blueprints = input.map { Blueprint.fromString(it) }.take(3)
    return blueprints.map { blueprint ->
        globalBest = 0
        val start = System.currentTimeMillis()
        val inventory = IntArray(Material.values().size)
        val robots = IntArray(Robot.values().size).also { it[Robot.ORE.ordinal] = 1 }
        val state = State(blueprint, 0, 32, inventory, robots)
        val geodes = collectGeodesRecursive(state)
        println("Blueprint $blueprint -> $geodes, time=${System.currentTimeMillis() - start} ms")
        geodes
    }.reduce { acc, i -> acc * i }
}

fun main() {
    val testInput = readInput("Day19_test", 2022)
    check(part1(testInput) == 33)
    check(part2(testInput) == 56 * 62)

    val input = readInput("Day19", 2022)
    println(part1(input))
    println(part2(input))
}
