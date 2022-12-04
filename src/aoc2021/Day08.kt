package aoc2021

import readInput

private fun part1(input: List<String>) =
    input.map { it.split("|").last().trim().split(" ") }.flatten().map { it.length }.filter {
        when (it) {
            2, 4, 3, 7 -> true
            else -> false
        }
    }.size


private enum class Digit(vararg chars: Char) {

    ZERO('a', 'b', 'c', 'e', 'f', 'g'),
    ONE('c', 'f'),
    TWO('a', 'c', 'd', 'e', 'g'),
    THREE('a', 'c', 'd', 'f', 'g'),
    FOUR('b', 'c', 'd', 'f'),
    FIVE('a', 'b', 'd', 'f', 'g'),
    SIX('a', 'b', 'd', 'e', 'f', 'g'),
    SEVEN('a', 'c', 'f'),
    EIGHT('a', 'b', 'c', 'd', 'e', 'f', 'g'),
    NINE('a', 'b', 'c', 'd', 'f', 'g');

    private val charArray = chars

    override fun toString() = this.ordinal.toString()

    /**
     * @param input the input signal
     * @param mapping a signal mapping
     * @return true, if this [Digit] is a valid solution for the given input signal and signal mapping
     */
    fun isPossible(input: String, mapping: Map<Char, Char>) =
        charArray.size == input.length && input.chars().mapToObj { it.toChar() }.map { mapping[it] }
            .allMatch { char -> char == null || charArray.contains(char) }

    companion object {
        /**
         * @param input the input signal
         * @param mapping a signal mapping
         * @return true, if the input can be mapped to at least one valid [Digit]
         */
        fun isValidMapping(input: String, mapping: Map<Char, Char>) = getDigit(input, mapping) != null

        /**
         * @param input the input signal
         * @param mapping a signal mapping
         * @return a [Digit] object which can be constructed with the given input. Might be null if the input can not be
         * mapped to a valid [Digit] (e.g. the mapping is invalid)
         */
        fun getDigit(input: String, mapping: Map<Char, Char>) = values().firstOrNull { it.isPossible(input, mapping) }
    }
}

/**
 * @param possibleValues all allowed [Char]s which shall be permutated
 * @return a sequence of permutations of the given input array
 */
private fun getPermutations(possibleValues: CharArray) = getPermutationsRec(possibleValues.size, possibleValues)
private fun getPermutationsRec(length: Int, possibleValues: CharArray): Sequence<CharArray> {
    return sequence {
        if (length == 1) {
            for (value in possibleValues) {
                yield(charArrayOf(value))
            }
        } else {
            for (value in possibleValues) {
                getPermutationsRec(length - 1, possibleValues).filter { !it.contains(value) }
                    .forEach { yield(charArrayOf(value, *it)) }
            }
        }
    }
}

/**
 * @param original the original characters
 * @param permutation the permutated characters
 * @return a mapping of the original characters to their counterpart in the permutation
 */
private fun getMappingFromPermutation(original: CharArray, permutation: CharArray) = original.zip(permutation).toMap()

private fun part2(input: List<String>): Int {

    val charArray = ('a'..'g').toList().toCharArray()
    val permutations = getPermutations(charArray)

    return input.sumOf { line ->
        val signals = line.split("|").first().trim().split(" ")
        val test = line.split("|").last().trim().split(" ")
        permutations.map { getMappingFromPermutation(charArray, it) }.firstOrNull { mapping ->
            signals.all { Digit.isValidMapping(it, mapping) } && test.all { Digit.isValidMapping(it, mapping) }
        }?.let { mapping -> test.joinToString("") { Digit.getDigit(it, mapping).toString() }.toInt() }
            ?: throw IllegalArgumentException("No valid signal mapping found")
    }
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
