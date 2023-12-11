package aoc2023

import Point
import readInput
import to2dCharArray

object Day11 {

    fun getDistanceSum(input: List<String>, emptyMultiplier: Int = 2): Long {
        val initialMap = input.to2dCharArray()
        val emptyColumns = initialMap.withIndex().filter { (x, column) -> column.all { it == '.' } }.map { it.index }
        val emptyRows = input.withIndex().filter { (y, row) -> row.all { it == '.' } }.map { it.index }

        val galaxies = initialMap.withIndex().flatMap { (x, column) ->
            column.withIndex().filter { (y, char) -> char == '#' }.map { (y, char) ->
                Point(
                    x + emptyColumns.filter { it < x }.size * (emptyMultiplier - 1),
                    y + emptyRows.filter { it < y }.size * (emptyMultiplier - 1)
                )
            }
        }

        val distances = galaxies.flatMap { g1 -> galaxies.filter { it != g1 }.map { g2 -> g1 to g2 } }.distinctBy {
            // order by x, then by y
            if (it.first.x < it.second.x || (it.first.x == it.second.x && it.first.y < it.second.y)) {
                "${it.first.x},${it.first.y}|${it.second.x},${it.second.y}"
            } else {
                "${it.second.x},${it.second.y}|${it.first.x},${it.first.y}"
            }
        }.map { it.first.longDistanceTo(it.second) }

        return distances.sum()
    }

    fun part1(input: List<String>) = getDistanceSum(input).toInt()

    fun part2(input: List<String>) = getDistanceSum(input, 1_000_000)
}

fun main() {
    val testInput = readInput("Day11_test", 2023)
    check(Day11.part1(testInput) == 374)
    check(Day11.getDistanceSum(testInput, 10) == 1030L)
    check(Day11.getDistanceSum(testInput, 100) == 8410L)

    val input = readInput("Day11", 2023)
    println(Day11.part1(input))
    println(Day11.part2(input))
}
