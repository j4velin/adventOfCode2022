package aoc2024

import readInput
import kotlin.collections.first
import kotlin.collections.last
import kotlin.collections.windowed
import kotlin.math.abs

object Day02 {

    private class Report(levels: List<Int>, applyProblemDampener: Boolean = false) {

        val isSafe = if (applyProblemDampener) {
            val toRemove = findFirstProblematicIndexes(levels)
            toRemove.any { index ->
                val newList = levels.toMutableList()
                newList.removeAt(index)
                isSafe(newList)
            }
        } else {
            isSafe(levels)
        }

        private fun findFirstProblematicIndexes(levels: List<Int>): List<Int> {
            val errors = levels.withIndex().windowed(size = 2, step = 1)
                .firstOrNull { abs(it.first().value - it.last().value) !in 1..3 }?.map { it.index }
            if (errors == null) {
                val firstIncrease = levels.withIndex().windowed(size = 2, step = 1)
                    .firstOrNull { it.first().value < it.last().value }?.map { it.index }
                val firstDecrease = levels.withIndex().windowed(size = 2, step = 1)
                    .firstOrNull { it.first().value > it.last().value }?.map { it.index }
                return (firstIncrease ?: emptyList()) + (firstDecrease ?: emptyList())
            } else {
                return errors
            }
        }

        private fun isSafe(levels: List<Int>): Boolean {
            var increasing = 0
            var decreasing = 0
            levels.windowed(size = 2, step = 1).forEach {
                if (abs(it.first() - it.last()) in 1..3) {
                    if (it.first() > it.last()) {
                        decreasing++
                    } else {
                        increasing++
                    }
                } else {
                    return false
                }
            }
            return increasing == 0 || decreasing == 0
        }
    }

    fun part1(input: List<String>): Int {
        return input.map { report -> Report(report.split("\\s+".toRegex()).map { it.toInt() }) }.filter { it.isSafe }
            .count()
    }

    fun part2(input: List<String>): Int {
        return input.map { report ->
            Report(
                levels = report.split("\\s+".toRegex()).map { it.toInt() },
                applyProblemDampener = true
            )
        }.filter { it.isSafe }.count()
    }
}

fun main() {
    val testInput = readInput("Day02_test", 2024)
    check(Day02.part1(testInput) == 2)
    check(Day02.part2(testInput) == 4)

    val input = readInput("Day02", 2024)
    println(Day02.part1(input))
    println(Day02.part2(input))
}
