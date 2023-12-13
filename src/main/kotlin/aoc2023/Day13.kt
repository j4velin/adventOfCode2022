package aoc2023

import readInput
import separateBy
import to2dCharArray

object Day13 {

    private fun searchHorizontalReflectionLine(input: Array<CharArray>, smudges: Int = 0): Int? {
        for (y in 1..<input.first().size) {
            var distanceToCheck = 0
            var remainingSmudges = smudges
            while (remainingSmudges >= 0) {
                distanceToCheck++
                if (y - distanceToCheck < 0 || y + distanceToCheck - 1 == input.first().size) {
                    if (remainingSmudges == 0) {
                        return y
                    } else {
                        break
                    }
                }
                remainingSmudges -= input.indices.count { x -> input[x][y - distanceToCheck] != input[x][y + distanceToCheck - 1] }
            }
        }
        return null
    }

    private fun searchVerticalReflectionLine(input: Array<CharArray>, smudges: Int = 0): Int? {
        for (x in 1..<input.size) {
            var distanceToCheck = 0
            var remainingSmudges = smudges
            while (remainingSmudges >= 0) {
                distanceToCheck++
                if (x - distanceToCheck < 0 || x + distanceToCheck - 1 == input.size) {
                    if (remainingSmudges == 0) {
                        return x
                    } else {
                        break
                    }
                }
                remainingSmudges -= input.first().indices.count { y -> input[x - distanceToCheck][y] != input[x + distanceToCheck - 1][y] }
            }
        }
        return null
    }

    private fun findReflectionValue(input: List<String>, smudges: Int = 0): Int {
        val array = input.to2dCharArray()
        val vertical = searchVerticalReflectionLine(array, smudges)
        return if (vertical != null) {
            vertical
        } else {
            val horizontal = searchHorizontalReflectionLine(array, smudges)
            if (horizontal != null) {
                horizontal * 100
            } else {
                throw IllegalArgumentException("No reflection line found for $input")
            }
        }
    }

    fun part1(input: List<String>) = input.separateBy { it.isEmpty() }.sumOf { findReflectionValue(it) }

    fun part2(input: List<String>) = input.separateBy { it.isEmpty() }.sumOf { findReflectionValue(it, smudges = 1) }
}

fun main() {
    val testInput = readInput("Day13_test", 2023)
    check(Day13.part1(testInput) == 405)
    check(Day13.part2(testInput) == 400)

    val input = readInput("Day13", 2023)
    println(Day13.part1(input))
    println(Day13.part2(input))
}
