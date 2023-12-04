package aoc2022

import readInput

fun main() {

    fun findMarkerIndex(input: String, distinctCount: Int): Int =
        input.withIndex().windowed(size = distinctCount, step = 1)
            .first { l -> l.distinctBy { it.value }.size == l.size }
            .last().index + 1

    fun part1(input: List<String>): Int = findMarkerIndex(input.first(), 4)

    fun part2(input: List<String>): Int = findMarkerIndex(input.first(), 14)

    val testInput = readInput("Day06_test", 2022)
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    val input = readInput("Day06", 2022)
    println(part1(input))
    println(part2(input))
}
