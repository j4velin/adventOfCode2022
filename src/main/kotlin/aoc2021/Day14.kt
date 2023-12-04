package aoc2021

import readInput

/**
 * @param input the input polymer string
 * @param rules a map of polymer transformation rules. Key=Input elements, Value=Element to be inserted between the input elements
 * @return a polymer string constructed by taking the [input] string an applying all the [rules] simultaneously
 */
private fun applyRules(input: String, rules: Map<String, Char>) =
    input.windowed(2).map { it[0] + rules.getOrDefault(it, it).toString() + it[1] }
        .foldIndexed(StringBuilder()) { index, acc, s ->
            if (index == 0) acc.append(s) else acc.append(s.drop(1))
        }.toString()

/**
 * Parses the input for to a map of polymer transformation rules
 */
private fun parseRules(input: List<String>) =
    input.drop(2).map { it.split(" -> ") }.associate { Pair(it[0], it[1].first()) }

/**
 * Calculates the result number for aoc2021.part1 & aoc2021.part2 based on the occurrences of each character in the final polymer string
 */
private fun calculateResult(occurrences: Map<Char, Long>) =
    occurrences.values.maxOf { it } - occurrences.values.minOf { it }

private fun part1(input: List<String>): Int {
    var current = input.first()
    val rules = parseRules(input)
    repeat(10) { current = applyRules(current, rules) }
    val occurrences = current.toCharArray().groupBy { it }.mapValues { it.value.size.toLong() }
    return calculateResult(occurrences).toInt()
}

/**
 * A polymer factory to generate a polymer string based on an initial polymer and a set of transformation rules.
 *
 * @param input the initial polymer string
 * @property rules the transformation rules. Key=Pair of characters, Value=Character to place in between the Key in a single step
 */
private class PolymerFactory(input: String, private val rules: Map<String, Char>) {
    private val lastCharacter = input.last()
    private val occurrences: MutableMap<String, Long> = HashMap(26 * 26)

    init {
        input.windowed(2).forEach { occurrences[it] = (occurrences[it] ?: 0L) + 1 }
    }

    /**
     * Performs one building step, e.g. applies all the rules once
     */
    fun performBuildStep() {
        val diff: MutableMap<String, Long> = HashMap(rules.size)
        for (rule in rules) {
            val currentCount = occurrences[rule.key] ?: 0L
            if (currentCount > 0L) {
                diff[rule.key] = (diff[rule.key] ?: 0L) - currentCount
                val newPair1 = String(charArrayOf(rule.key[0], rule.value))
                val newPair2 = String(charArrayOf(rule.value, rule.key[1]))
                diff[newPair1] = (diff[newPair1] ?: 0L) + currentCount
                diff[newPair2] = (diff[newPair2] ?: 0L) + currentCount
            }
        }
        diff.forEach { occurrences[it.key] = (occurrences[it.key] ?: 0L) + it.value }
    }

    /**
     * @return the number of occurrences of each character in the current polymer
     */
    fun getOccurrences(): Map<Char, Long> {
        val result: MutableMap<Char, Long> = HashMap(26)
        occurrences.forEach { result[it.key.first()] = (result[it.key.first()] ?: 0L) + it.value }
        result[lastCharacter] = (result[lastCharacter] ?: 0) + 1
        return result
    }

}

private fun part2(input: List<String>): Long {
    val rules = parseRules(input)
    val factory = PolymerFactory(input.first(), rules)
    repeat(40) {
        factory.performBuildStep()
    }
    return calculateResult(factory.getOccurrences())
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)
    check(part2(testInput) == 2188189693529L)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
