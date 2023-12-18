package aoc2023

import PointL
import areaWithin
import readInput

@OptIn(ExperimentalStdlibApi::class)
object Day18 {

    /**
     * @param input the puzzle input
     * @param getDelta a function to transform a given input line into a [PointL]-Delta (to get to the next point)
     */
    private fun solve(input: List<String>, getDelta: (String) -> PointL): Long {
        var current = PointL(0L, 0L)
        val edges = buildList {
            add(current)
            addAll(input.map { line -> current += getDelta(line); current })
        }

        val border = edges.windowed(2, 1).sumOf { it.first().distanceTo(it.last()) }

        // Pick's theorem is actually A = inner + border/2 - 1
        // -> adding 2 (+1 instead of -1) seems to get the correct answers, probably due to the corners somehow
        return edges.areaWithin() + border / 2 + 1
    }

    fun part1(input: List<String>) = solve(input) { line ->
        val split = line.split(" ")
        val direction = split[0]
        val length = split[1].toLong()
        when (direction) {
            "U" -> PointL(0, -length)
            "D" -> PointL(0, length)
            "L" -> PointL(-length, 0)
            "R" -> PointL(length, 0)
            else -> throw IllegalArgumentException("Unknown direction: $direction")
        }
    }.toInt()

    fun part2(input: List<String>) = solve(input) { line ->
        // drop '(#' and ')'
        val color = line.substring(line.lastIndexOf('(')).drop(2).take(6)
        val direction = color.last()
        val length = color.take(5).hexToLong()
        // 0 means R, 1 means D, 2 means L, and 3 means U.
        when (direction) {
            '3' -> PointL(0, -length)
            '1' -> PointL(0, length)
            '2' -> PointL(-length, 0)
            '0' -> PointL(length, 0)
            else -> throw IllegalArgumentException("Unknown direction: $direction")
        }
    }
}

fun main() {
    val testInput = readInput("Day18_test", 2023)
    check(Day18.part1(testInput) == 62)
    check(Day18.part2(testInput) == 952408144115L)

    val input = readInput("Day18", 2023)
    println(Day18.part1(input))
    println(Day18.part2(input))
}
