package aoc2024

import readInput
import kotlin.math.abs

object Day01 {

    private fun parseLists(input: List<String>): Pair<List<Int>, List<Int>> {
        val left = mutableListOf<Int>()
        val right = mutableListOf<Int>()
        val regex = "\\s+".toRegex()
        input.forEach { line ->
            val lineItems = line.split(regex).map { number -> number.toInt() }
            left.add(lineItems.first())
            right.add(lineItems.last())
        }
        return left.sorted() to right.sorted()
    }

    fun part1(input: List<String>): Int {
        val (left, right) = parseLists(input)
        return left.indices.sumOf { abs(left[it] - right[it]) }
    }

    fun part2(input: List<String>): Int {
        val (left, right) = parseLists(input)
        val occurrencesCount = left.associateWith { 0 }.toMutableMap()
        right.filter { it in occurrencesCount.keys }.forEach {
            occurrencesCount[it] = occurrencesCount[it]!! + 1
        }
        return left.sumOf { it * occurrencesCount[it]!! }
    }
}

fun main() {
    val testInput = readInput("Day01_test", 2024)
    check(Day01.part1(testInput) == 11)
    check(Day01.part2(testInput) == 31)

    val input = readInput("Day01", 2024)
    println(Day01.part1(input))
    println(Day01.part2(input))
}
