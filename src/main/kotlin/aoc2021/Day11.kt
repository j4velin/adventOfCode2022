package aoc2021

import readInput
import java.util.*
import kotlin.math.max
import kotlin.math.min

typealias Octopus = Pair<Int, Int>

private const val ENERGY_THRESHOLD = 9

private class OctopusArray(input: List<String>) {

    private val maxX: Int
    private val maxY: Int

    /**
     * The number of octopuses in this array
     */
    val elementCount: Int

    // internally represented as a 2D Byte array
    private val data = input.map { str -> str.map { it.digitToInt().toByte() }.toByteArray() }.toTypedArray()

    init {
        maxX = data.size - 1
        maxY = (data.firstOrNull()?.size ?: 0) - 1
        elementCount = data.size * (maxY + 1)
    }

    /**
     * Performs a 'step': Increases all levels & flashes all octopuses, which have or get the required energy level
     *
     * @return the number of octopuses, which have flashed during this step
     */
    fun step(): Int {
        val readyToFlash = increaseAllLevels()
        val flashed = flashAll(readyToFlash)
        // reset the energy level of the flashed ones
        flashed.forEach { data[it.first][it.second] = 0 }
        return flashed.size
    }

    /**
     * Increases all octopuses energy levels
     * @return the positions of all octopuses, which now have an energy level > [ENERGY_THRESHOLD]
     */
    private fun increaseAllLevels() = increaseArea(Pair(0, 0), Pair(maxX, maxY))

    /**
     * Increases the energy level of the center octopus and all the octopuses in its surrounding neighbourhood
     *
     * @param center the position of the octopus in the center
     * @return a collection of all octopuses which became ready to flash during this increase operation
     */
    private fun increaseNeighbours(center: Octopus) =
        increaseArea(Pair(center.first - 1, center.second - 1), Pair(center.first + 1, center.second + 1))

    /**
     * Increases the energy level of the octopuses covered by the given area
     *
     * @param topLeft the top left position of the area
     * @param bottomRight the bottom right position of the area
     * @return a collection of all octopuses which became ready to flash during this increase operation
     */
    private fun increaseArea(topLeft: Pair<Int, Int>, bottomRight: Pair<Int, Int>): Collection<Octopus> {
        val readyToFlash = mutableSetOf<Octopus>()
        for (i in max(0, topLeft.first)..min(maxX, bottomRight.first)) {
            for (j in max(0, topLeft.second)..min(maxY, bottomRight.second)) {
                if (++data[i][j] > ENERGY_THRESHOLD) {
                    readyToFlash.add(Pair(i, j))
                }
            }
        }
        return readyToFlash
    }

    /**
     * Flashes all ready octopuses, which are ready to flash or become ready to flash in the process of flashing its
     * neighbours
     *
     * @param init an initial list of positions of octopuses to flash
     * @return a collection of all the flashed octopuses
     */
    private fun flashAll(init: Collection<Octopus>): Collection<Octopus> {
        val flashed = mutableSetOf<Octopus>()
        val readyToFlash: Queue<Octopus> = LinkedList()
        readyToFlash.addAll(init)
        while (readyToFlash.isNotEmpty()) {
            val flashing = readyToFlash.poll()
            // consider neighbours only, if we haven't already flashed that octopus in this step
            if (flashed.add(flashing)) {
                readyToFlash.addAll(increaseNeighbours(flashing))
            }
        }
        return flashed
    }

}

private fun part1(input: List<String>): Int {
    val array = OctopusArray(input)
    var flashCount = 0
    repeat(100) { flashCount += array.step() }
    return flashCount
}

private fun part2(input: List<String>): Int {
    val array = OctopusArray(input)
    var step = 1
    while (array.step() < array.elementCount) {
        step++
    }
    return step
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
