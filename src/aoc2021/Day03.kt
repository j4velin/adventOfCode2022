package aoc2021

import readInput
import kotlin.math.ceil

/**
 * Given a list of binary numbers, this method creates a binary number by selecting the most common bit at each position
 * of the input numbers
 *
 * @param input a list of equally width binary numbers as string
 * @return a string representation of the binary number constructed of the most common bits of each of the input numbers
 * for each position
 */
private fun getMostCommonBits(input: List<String>): String {
    val bits = input.first().length
    val maxIndex = bits - 1
    // count how often each bit is set
    val setBits = IntArray(bits)
    input.map { it.toInt(2) }.forEach {
        for (i in 0..maxIndex) {
            setBits[i] += (it shr maxIndex - i) and 1
        }
    }
    val threshold = ceil(input.size / 2f)
    return buildString {
        setBits.withIndex().forEach {
            when {
                it.value >= threshold -> append(1)
                else -> append(0)
            }
        }
    }
}

/**
 * @return for a string representing a binary number, this method returns a new string with each bit flipped
 */
private fun String.inv() = this.replace("1", "3").replace("0", "1").replace("3", "0")

/**
 * Iteratively shrinks the given input list by applying a filter function on all elements, until only one element remains
 *
 * @param input     the input list to shrink
 * @param filter    the filter function to apply in each iteration. Input for the filter function is the currently selected
 *                  bit of an input number and the most common bit for that position among all remaining input numbers
 * @return the element passing the most filters
 */
private fun shrinkList(input: List<String>, filter: (Char, Char) -> Boolean): String {
    var filteredList = input
    var currentBit = 0
    while (filteredList.size > 1) {
        val mostCommonBits = getMostCommonBits(filteredList)
        filteredList = filteredList.filter { filter(it[currentBit], mostCommonBits[currentBit]) }
        currentBit++
    }
    return filteredList.first()
}

private fun part1(input: List<String>): Int {
    val mostCommonBits = getMostCommonBits(input)
    val gamma = mostCommonBits.toInt(2)
    val epsilon = mostCommonBits.inv().toInt(2)
    return gamma * epsilon
}

private fun part2(input: List<String>): Int {
    val oxygen = shrinkList(input) { c1, c2 -> c1 == c2 }.toInt(2)
    val co2 = shrinkList(input) { c1, c2 -> c1 != c2 }.toInt(2)
    return oxygen * co2
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
