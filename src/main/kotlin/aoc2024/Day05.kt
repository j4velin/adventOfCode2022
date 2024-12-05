package aoc2024

import readInput

object Day05 {

    private data class Rule(val before: Int, val after: Int)
    private data class Update(val pages: List<Int>) {
        val middlePage = pages[pages.size / 2]

        fun isInCorrectOrder(rules: List<Rule>) =
            rules.filter { pages.contains(it.before) && pages.contains(it.after) }.all { rule ->
                pages.indexOf(rule.before) < pages.indexOf(rule.after)
            }

        fun fixOrder(rules: List<Rule>): Update {
            val relevantRules = rules.filter { pages.contains(it.before) && pages.contains(it.after) }
            val newOrder = pages.sortedWith(object : Comparator<Int> {
                override fun compare(o1: Int, o2: Int): Int {
                    val r1 = relevantRules.firstOrNull { it.before == o1 && it.after == o2 }
                    if (r1 != null) {
                        return -1
                    }
                    val r2 = relevantRules.firstOrNull { it.before == o2 && it.after == o1 }
                    if (r2 != null) {
                        return 1
                    }
                    return 0
                }
            })
            return Update(newOrder)
        }
    }

    fun part1(input: List<String>): Int {
        val (rules, updates) = parseInput(input)
        return updates.filter { it.isInCorrectOrder(rules) }.map { it.middlePage }.sumOf { it }
    }

    fun part2(input: List<String>): Int {
        val (rules, updates) = parseInput(input)
        return updates.filter { !it.isInCorrectOrder(rules) }.map { it.fixOrder(rules) }.map { it.middlePage }
            .sumOf { it }
    }

    private fun parseInput(input: List<String>): Pair<List<Rule>, List<Update>> {
        val rules = input.takeWhile { it.isNotBlank() }.map { it.split("|").map { it.toInt() } }
            .map { Rule(it.first(), it.last()) }
        val updates = input.drop(rules.size + 1).map { Update(it.split(",").map { it.toInt() }) }
        return rules to updates
    }
}

fun main() {
    val testInput = readInput("Day05_test", 2024)
    check(Day05.part1(testInput) == 143)
    check(Day05.part2(testInput) == 123)

    val input = readInput("Day05", 2024)
    println(Day05.part1(input))
    println(Day05.part2(input))
}
