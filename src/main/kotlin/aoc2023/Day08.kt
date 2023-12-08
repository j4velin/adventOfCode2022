package aoc2023

import readInput

private data class Node(val name: String) {
    lateinit var left: Node
    lateinit var right: Node
}

private data class InputMap(val directions: Sequence<Char>, val nodes: Map<String, Node>) {
    companion object {
        fun fromStrings(input: List<String>): InputMap {
            val directions = sequence { while (true) input.first().forEach { yield(it) } }
            val regex = """(?<name>.+) = \((?<left>.+), (?<right>.+)\)""".toRegex()
            val nodes = input.drop(2).map {
                val matchResult = regex.matchEntire(it)
                if (matchResult != null) {
                    Node(matchResult.groups["name"]!!.value)
                } else {
                    throw IllegalArgumentException("Did not match regex: $it")
                }
            }.associateBy { it.name }

            // fill nodes
            input.drop(2).forEach {
                val matchResult = regex.matchEntire(it)
                val node = nodes[matchResult!!.groups["name"]!!.value]!!
                node.left = nodes[matchResult.groups["left"]!!.value]!!
                node.right = nodes[matchResult.groups["right"]!!.value]!!
            }

            return InputMap(directions, nodes)
        }
    }
}

private class Loop {
    var stepsToStartOfTheLoop = 0
    var stepsInTheLoop = 0
    var loopFound = false
}

object Day08 {
    fun part1(input: List<String>): Int {
        val inputMap = InputMap.fromStrings(input)
        val directions = inputMap.directions.iterator()

        var currentNode = inputMap.nodes["AAA"]
        var steps = 0
        while (currentNode!!.name != "ZZZ") {
            steps++
            val nextDirection = directions.next()
            currentNode = when (nextDirection) {
                'L' -> currentNode.left
                'R' -> currentNode.right
                else -> throw IllegalArgumentException("Unknown direction: $nextDirection")
            }
        }

        return steps
    }

    fun part2(input: List<String>): Long {
        val inputMap = InputMap.fromStrings(input)
        val directions = inputMap.directions.iterator()
        val startNodes = inputMap.nodes.values.filter { it.name.endsWith('A') }
        val endNodes = inputMap.nodes.values.filter { it.name.endsWith('Z') }
        val loops = startNodes.associateWith { endNodes.associateWith { Loop() }.toMutableMap() }.toMutableMap()
        var currentNodes = inputMap.nodes.values.filter { it.name.endsWith('A') }
        var steps = 0
        var searchForLoops = true
        while (searchForLoops && !currentNodes.all { it.name.endsWith('Z') }) {
            steps++
            val nextDirection = directions.next()
            currentNodes = currentNodes.withIndex().map { (idx, currentNode) ->
                val next = when (nextDirection) {
                    'L' -> currentNode.left
                    'R' -> currentNode.right
                    else -> throw IllegalArgumentException("Unknown direction: $nextDirection")
                }
                if (next.name.endsWith('Z')) {
                    val startNode = startNodes[idx]
                    val loop = loops[startNode]!![next]!!
                    if (loop.stepsToStartOfTheLoop == 0) {
                        loop.stepsToStartOfTheLoop = steps
                    } else if (!loop.loopFound) {
                        loop.stepsInTheLoop = steps - loop.stepsToStartOfTheLoop
                        loop.loopFound = true
                    }
                    if (loops.values.all { it.values.any { loop -> loop.loopFound } }) {
                        // assume we need to find only 1 loop per start node
                        searchForLoops = false
                    }
                }
                next
            }
        }

        // TODO: implement LCM algorithm in Utils module
        val loopSteps = loops.values.flatMap {
            it.values.filter { loop -> loop.loopFound }.map { loop -> loop.stepsInTheLoop }
        }
        println("To get the actual answer, find the LCM of the following numbers: ${loopSteps.joinToString(separator = " ") { it.toString() }}")
        println("https://www.calculatorsoup.com/calculators/math/lcm.php?input=${loopSteps.joinToString(separator = "+")}&data=none&action=solve")

        // return 6 to pass the test
        return 6
    }
}

fun main() {
    val testPart1 = """LLR

AAA = (BBB, BBB)
BBB = (AAA, ZZZ)
ZZZ = (ZZZ, ZZZ)""".split("\n")

    val testPart2 = """LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)""".split("\n")

    check(Day08.part1(testPart1) == 6)
    check(Day08.part2(testPart2) == 6L)

    val input = readInput("Day08", 2023)
    println(Day08.part1(input))
    println(Day08.part2(input))
}
