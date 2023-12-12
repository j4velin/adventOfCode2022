package aoc2023

import readInput

private class SpringRow(private val springs: String, damagedGroups: IntArray) {
    companion object {
        fun fromString(input: String): SpringRow {
            val split = input.split(" ")
            return SpringRow(split[0], split[1].split(",").map { it.toInt() }.toIntArray())
        }

        fun fromPart2String(input: String): SpringRow {
            val split = input.split(" ")
            val springs = (split[0] + "?").repeat(5).dropLast(1)
            val damagedGroups = (split[1] + ",").repeat(5).dropLast(1).split(",").map { it.toInt() }.toIntArray()
            return SpringRow(springs, damagedGroups)
        }
    }

    // Part 1
    val arrangements by lazy { findPossibleArrangements(springs) }
    private val regex = damagedGroups.map { length -> "(#|\\?)".repeat(length) }
        .joinToString(separator = "(\\.|\\?)+", prefix = "(\\.|\\?)*", postfix = "(\\.|\\?)*") { it }.toRegex()

    private fun findPossibleArrangements(input: String): List<String> {
        if (!input.matches(regex)) return emptyList()
        if (!input.contains('?')) return listOf(input)

        val test1 = input.replaceFirst('?', '.')
        val test2 = input.replaceFirst('?', '#')

        return findPossibleArrangements(test1) + findPossibleArrangements(test2)
    }

    // Part 2
    val arrangementsPart2 by lazy {
        var currentBlock = 1
        var regex = damagedGroups.take(currentBlock).map { length -> "(#|\\?)".repeat(length) }
            .joinToString(separator = "(\\.|\\?)+", prefix = "(\\.|\\?)*", postfix = "(\\.|\\?)*") { it }.toRegex()
        for (currentPosition in springs.indices) {
            val matches = getNumberOfMatches(springs.take(currentPosition), currentBlock, regex)
            if (matches > 0) {
                currentBlock++
                regex = damagedGroups.take(currentBlock).map { length -> "(#|\\?)".repeat(length) }
                    .joinToString(separator = "(\\.|\\?)+", prefix = "(\\.|\\?)*", postfix = "(\\.|\\?)*") { it }
                    .toRegex()
            }
        }
        getNumberOfMatches(springs, damagedGroups.size, regex)
    }
    private val cache = mutableMapOf<Pair<String, Int>, Long>()

    private fun getNumberOfMatches(input: String, groupsToMatch: Int, regex: Regex): Long {
        val shrinked = input.replace("\\.+".toRegex(), ".")
        if (!shrinked.matches(regex)) return 0
        if (!shrinked.contains('?')) return 1
        if (cache.contains(shrinked to groupsToMatch)) return cache[shrinked to groupsToMatch]!!

        // matches but not in cache yet
        val test1 = shrinked.replaceFirst('?', '.')
        val test2 = shrinked.replaceFirst('?', '#')

        val matches = getNumberOfMatches(test1, groupsToMatch, regex) + getNumberOfMatches(test2, groupsToMatch, regex)
        return matches.also { cache[shrinked to groupsToMatch] = it }
    }
}

object Day12 {
    fun part1(input: List<String>) = input.map { SpringRow.fromString(it) }.sumOf { it.arrangements.size }

    fun part2(input: List<String>) = input.map { SpringRow.fromPart2String(it) }.sumOf { it.arrangementsPart2 }
}

fun main() {
    val testInput = readInput("Day12_test", 2023)
    check(Day12.part1(testInput) == 21)
    check(Day12.part2(testInput) == 525152L)

    val input = readInput("Day12", 2023)
    println(Day12.part1(input))
    println("\n" + Day12.part2(input))
}
