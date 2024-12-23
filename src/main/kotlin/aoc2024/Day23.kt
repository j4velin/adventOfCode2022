package aoc2024

import readInput
import withEachOf

private typealias Computer = String

object Day23 {

    fun part1(input: List<String>): Int {
        val connections = mutableMapOf<Computer, MutableSet<Computer>>()
        input.forEach { connection ->
            val (computer1, computer2) = connection.split("-")
            connections[computer1]?.add(computer2) ?: run { connections[computer1] = mutableSetOf(computer2) }
            connections[computer2]?.add(computer1) ?: run { connections[computer2] = mutableSetOf(computer1) }
        }

        val setsOfThree = mutableSetOf<List<Computer>>()

        connections.forEach { (computer, connectedComputers) ->
            connectedComputers.withEachOf(connectedComputers).forEach { (c1, c2) ->
                if (connections[c1]!!.contains(c2) || connections[c2]!!.contains(c1)) {
                    setsOfThree.add(listOf(computer, c1, c2).sorted())
                }
            }
        }

        return setsOfThree.count { computers -> computers.any { it.startsWith("t") } }
    }

    fun part2(input: List<String>): String {
        val connections = mutableMapOf<Computer, MutableSet<Computer>>()
        val stronglyConnected = mutableListOf<MutableSet<Computer>>()
        input.forEach { connection ->
            val (computer1, computer2) = connection.split("-")
            connections[computer1]?.add(computer2) ?: run { connections[computer1] = mutableSetOf(computer2) }
            stronglyConnected.add(mutableSetOf(computer1, computer2))
        }

        val isConnected: (Computer, Computer) -> Boolean =
            { c1, c2 -> connections[c1]!!.contains(c2) || connections[c2]!!.contains(c1) }

        connections.keys.forEach { computer ->
            stronglyConnected.forEach { set ->
                if (set.all { isConnected(it, computer) }) {
                    set.add(computer)
                }
            }
        }

        return stronglyConnected.map { it.sorted() }.toSet().maxBy { it.size }.joinToString(separator = ",")
    }
}

fun main() {
    val testInput = readInput("Day23_test", 2024)
    check(Day23.part1(testInput) == 7)
    check(Day23.part2(testInput) == "co,de,ka,ta")

    val input = readInput("Day23", 2024)
    println(Day23.part1(input))
    println(Day23.part2(input))
}
