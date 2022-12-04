package aoc2022

import findCommon
import readInput

private fun findCommonItem(vararg compartments: String): Char =
    findCommon(*compartments.map { it.toCharArray().toSet() }.toTypedArray())

private val Char.priority: Int
    get() = if (isLowerCase()) {
        this - 'a' + 1
    } else {
        this - 'A' + 27
    }

fun main() {

    fun part1(input: List<String>): Int {
        return input.sumOf { rucksack ->
            val compartment1 = rucksack.substring(0, rucksack.length / 2)
            val compartment2 = rucksack.substring(rucksack.length / 2)
            val item = findCommonItem(compartment1, compartment2)
            item.priority
        }
    }

    fun part2(input: List<String>): Int {
        return input.windowed(3, 3).sumOf { elves ->
            val item = findCommonItem(elves[0], elves[1], elves[2])
            item.priority
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
