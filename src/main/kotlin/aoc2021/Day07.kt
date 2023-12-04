package aoc2021

import readInput
import kotlin.math.abs
import kotlin.math.min

/**
 * Transforms this [IntArray] so that at each index in the result array gives the amount of occurrences of that index
 * value in the original array
 *
 * For example: [0, 0, 2] -> [2, 0, 1]
 * -> original array contained two zeros -> result[0] = 2
 *
 * @return the consolidated array
 */
private fun IntArray.consolidate(): IntArray {
    val consolidated = IntArray(maxOf { it } + 1)
    groupBy { it }.mapValues { it.value.count() }.forEach { consolidated[it.key] = it.value }
    return consolidated
}

private fun IntArray.getMedianIndex(): Int {
    val elements = sum()
    var elementsSoFar = 0
    return withIndex().takeWhile {
        elementsSoFar += it.value
        elementsSoFar <= elements / 2
    }.last().index
}

/**
 * @param positions the horizontal positions (index) and the amount of submarines at that position (value)
 * @param destinationIndex the target position where to move all submarines
 * @param consumptionRate a function to calculate the fuel necessary for one submarine to move a given distance
 * @return the total fuel necessary to move all submarines to [destinationIndex]
 */
private inline fun getTotalFuelConsumption(
    positions: IntArray,
    destinationIndex: Int,
    consumptionRate: (Int) -> Int
) = positions.withIndex().sumOf { it.value * consumptionRate(abs(destinationIndex - it.index)) }

private fun part1(input: List<String>): Int {
    val array = input.first().split(",").map { it.toInt() }.toIntArray().consolidate()
    val median = array.getMedianIndex() // small optimization: target index should be around the median
    var minConsumption = Int.MAX_VALUE
    for (i in -1..1) {
        minConsumption = min(minConsumption, getTotalFuelConsumption(array, median + i) { distance -> distance })
    }
    return minConsumption
}

private fun part2(input: List<String>): Int {
    val array = input.first().split(",").map { it.toInt() }.toIntArray().consolidate()
    var minConsumption = Int.MAX_VALUE
    for (i in array.indices) {
        val consumption = getTotalFuelConsumption(array, i) { distance -> 0.rangeTo(distance).sum() }
        if (consumption < minConsumption) {
            minConsumption = consumption
        } else {
            break
        }
    }
    return minConsumption
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
