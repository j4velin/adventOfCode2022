package aoc2022

import readInput

fun main() {

    fun getBounds(section: String): IntRange {
        val (lower, upper) = section.split("-", limit = 2).map { it.toInt() }
        return IntRange(lower, upper)
    }

    fun part1(input: List<String>): Int {
        return input.map {
            val (section1, section2) = it.split(",", limit = 2)
            val range1 = getBounds(section1)
            val range2 = getBounds(section2)
            if (range1.intersect(range2).size == range1.count() || range2.intersect(range1).size == range2.count()) 1
            else 0
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.map {
            val (section1, section2) = it.split(",", limit = 2)
            val range1 = getBounds(section1)
            val range2 = getBounds(section2)
            if (range1.intersect(range2).isNotEmpty() || range2.intersect(range1).isNotEmpty()) 1
            else 0
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test", 2022)
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04", 2022)
    println(part1(input))
    println(part2(input))
}
