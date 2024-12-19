package aoc2024

import readInput

object Day19 {
    fun part1(input: List<String>): Int {
        val towels = input.first().split(", ")
        val patterns = input.drop(2)

        val regex = towels.joinToString(separator = "|", prefix = "(", postfix = ")*").toRegex()
        return patterns.count { regex.matches(it) }
    }

    private fun findAllMatches(remainingPattern: String, towels: List<String>, cache: MutableMap<String, Long>): Long {
        return cache[remainingPattern] ?: run {
            val sum = if (remainingPattern.isEmpty()) {
                1L
            } else {
                towels.filter { remainingPattern.startsWith(it) }.sumOf {
                    findAllMatches(remainingPattern.removePrefix(it), towels, cache)
                }
            }
            sum.also { cache[remainingPattern] = it }
        }
    }

    fun part2(input: List<String>): Long {
        val towels = input.first().split(", ")
        val patterns = input.drop(2)

        val regex = towels.joinToString(separator = "|", prefix = "(", postfix = ")*").toRegex()

        return patterns.filter { regex.matches(it) }.withIndex()
            .sumOf { pattern -> findAllMatches(pattern.value, towels, mutableMapOf()).toLong() }
    }
}

fun main() {
    val testInput = readInput("Day19_test", 2024)
    check(Day19.part1(testInput) == 6)
    check(Day19.part2(testInput) == 16L)

    val input = readInput("Day19", 2024)
    println(Day19.part1(input))
    println(Day19.part2(input))
}
