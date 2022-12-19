package aoc2022

import readInput
import kotlin.math.ceil
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
}

private data class Duration(val robot: Robot, val ticks: Int)

private data class State(
    val blueprint: Blueprint,
    val inventory: IntArray,
    val robots: IntArray
) {
    fun canBuild(robot: Robot) =
        blueprint.costs[robot]!!.all { (material, amount) -> inventory[material.ordinal] >= amount }

    fun getBuildDuration(): List<Duration> {

        // for GEODE
        val costGeode = blueprint.costs[Material.GEODE]!!
        val obsidianForGeode = costGeode.find { it.type == Material.OBSIDIAN }!!.amount
        val oreForGeode = costGeode.find { it.type == Material.ORE }!!.amount

        val timeForGeode = if (robots[Robot.OBSIDIAN.ordinal] > 0) {
            Duration(
                Robot.GEODE,
                max(obsidianForGeode / robots[Robot.OBSIDIAN.ordinal], oreForGeode / robots[Robot.ORE.ordinal])
            )
        } else {
            Duration(Robot.GEODE, Int.MAX_VALUE)
        }

        // for Obsidian
        val costObsidian = blueprint.costs[Material.OBSIDIAN]!!
        val clayForObsidian = costObsidian.find { it.type == Material.CLAY }!!.amount
        val oreForObsidian = costObsidian.find { it.type == Material.ORE }!!.amount

        val timeForObsidian = if (robots[Robot.CLAY.ordinal] > 0) {
            Duration(
                Robot.OBSIDIAN,
                max(clayForObsidian / robots[Robot.CLAY.ordinal], oreForObsidian / robots[Robot.ORE.ordinal])
            )
        } else {
            Duration(Robot.OBSIDIAN, Int.MAX_VALUE)
        }

        // for Clay
        val costClay = blueprint.costs[Material.CLAY]!!
        val oreForClay = costClay.find { it.type == Material.ORE }!!.amount

        val timeForClay = Duration(Robot.CLAY, oreForClay / robots[Robot.ORE.ordinal])

        // for Ore
        val costOre = blueprint.costs[Material.ORE]!!
        val oreForOre = costOre.find { it.type == Material.ORE }!!.amount

        val timeForOre = Duration(Robot.ORE, oreForOre / robots[Robot.ORE.ordinal])

        val priority = listOf(timeForGeode, timeForObsidian, timeForClay, timeForOre)

        return priority.sortedByDescending { it.ticks }
    }

    fun harvest(): State {
        val newInventory = inventory.clone()
        Robot.values().forEach { type ->
            newInventory[type.ordinal] = inventory[type.ordinal] + robots[type.ordinal]
        }
        return State(blueprint, newInventory, robots)
    }

    fun build(robot: Robot): State {
        val newInventory = inventory.clone()
        val newRobots = robots.clone()
        blueprint.costs[robot]!!.forEach { (material, amount) ->
            newInventory[material.ordinal] = inventory[material.ordinal] - amount
        }
        newRobots[robot.ordinal] = robots[robot.ordinal] + 1
        return State(blueprint, newInventory, newRobots)
    }
}

private fun collectGeodes(minute: Int, state: State): Int {

    println("=== $minute ===")
    println("State: $state")
    val remainingTime = 24 - minute
    if (remainingTime == 0) {
        val s = state.harvest()
        println()
        println("\t\tFINAL STATE: GEODE = ${s.inventory[Material.GEODE.ordinal]} -> $s")
        println()
        return s.inventory[Material.GEODE.ordinal]
    }

    val durations = state.getBuildDuration()//.map { it.robot }
    println("   durations: $durations")

    val calculateWaitingTime: (Robot, State) -> Int = { robot, s ->
        s.blueprint.costs[robot]!!.maxOfOrNull { c ->
            val missing = c.amount - s.inventory[c.type.ordinal]
            val div = s.robots[c.type.ordinal]
            if (div == 0) Int.MAX_VALUE else ceil(missing / div.toFloat()).toInt()
        } ?: Int.MAX_VALUE
    }

    val wouldReduceTimeFor: (Robot, Material) -> Boolean = { robot, material ->
        val t0 = calculateWaitingTime(material, state)
        val tmpState = state.harvest().build(robot)
        var t1 = calculateWaitingTime(material, tmpState)
        if (t1 != Int.MAX_VALUE) {
            t1++
            println("   building $robot would change waiting time for $material from ${if (t0 == Int.MAX_VALUE) "∞" else t0} to $t1")
            t0 - t1 >= 0 || (t0 - t1 == -1 && remainingTime > 16) // TODO: increasing the build time now might be beneficial if there is much time left
        } else {
            false
        }
    }

    var tobuild: Robot? = null
    val canBuild = durations.filter { state.canBuild(it.robot) }
    println("   can build: $canBuild")
    canBuild.forEach {
        if (tobuild == null) {
            if (it.robot == Robot.GEODE) {
                tobuild = it.robot
            } else if ((it.robot == Robot.OBSIDIAN || it.robot == Robot.ORE) &&
                wouldReduceTimeFor(it.robot, Material.GEODE)
            ) {
                println(" -> reduces time to GEODE -> build ${it.robot}")
                tobuild = it.robot
            } else if ((it.robot == Robot.CLAY || it.robot == Robot.ORE) &&
                wouldReduceTimeFor(Robot.OBSIDIAN, Material.GEODE)
            ) {
                if (wouldReduceTimeFor(it.robot, Material.OBSIDIAN)) {
                    tobuild = it.robot
                    println(" -> reduces time to OBSIDIAN which reduces time to GEODE -> build ${it.robot}")
                } else if (it.robot == Robot.ORE && wouldReduceTimeFor(Robot.CLAY, Material.OBSIDIAN) &&
                    wouldReduceTimeFor(it.robot, Material.CLAY)
                ) {
                    tobuild = it.robot
                    println(" -> reduces time to CLAY, which reduces time to OBSIDIAN which reduces time to GEODE -> build ${it.robot}")
                }
            }
        }
    }

    // every robot harvests its material
    val stateHarvesting = state.harvest()

    // build, if there is a robot to build
    val stateBuilding = if (tobuild != null && state.canBuild(tobuild!!)) {
        println(" building $tobuild")
        stateHarvesting.build(tobuild!!)
    } else {
        stateHarvesting
    }
    println("State: $stateBuilding")

    return collectGeodes(minute + 1, stateBuilding)
}

private fun part1(input: List<String>): Int {
    val blueprints = input.map { Blueprint.fromString(it) }
    val sumOfAllQualityLevels = blueprints.sumOf { blueprint ->
        println("$blueprint (${blueprint.costs})")
        val inventory = IntArray(Material.values().size)
        val robots = IntArray(Robot.values().size).also { it[Robot.ORE.ordinal] = 1 }
        val scenario = State(
            blueprint,
            inventory,
            robots,
        )
        val geodes = collectGeodes(1, scenario)
        geodes * blueprint.id
    }
    println("sumOfAllQualityLevels: $sumOfAllQualityLevels")
    return sumOfAllQualityLevels
}

private fun part2(input: List<String>): Int {
    return 1
}

fun main() {
    val testInput = readInput("Day19_test", 2022)
    check(part1(testInput) == 33)
    check(part2(testInput) == 0)

    val input = readInput("Day19", 2022)
    println(part1(input))
    println(part2(input))
}
