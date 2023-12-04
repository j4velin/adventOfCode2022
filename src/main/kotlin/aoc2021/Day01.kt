package aoc2021

import readInput
import java.util.*

/**
 * @return a sequence of the result of the [compareTo] invocation on each element and its predecessor
 */
private fun <T : Comparable<T>> Sequence<T>.compareWithPrevious(): Sequence<Int> {
    val input = this
    return sequence {
        yield(0)
        var previous = input.first()
        input.drop(1).forEach {
            yield(it.compareTo(previous))
            previous = it
        }
    }
}

/**
 * Represents a sliding window of measurement data.
 *
 * @property measurements all the measurements in this window. New measurements can be added using the [add] method
 */
private data class MeasurementWindow(private val measurements: MutableList<Int> = mutableListOf()) :
    Comparable<MeasurementWindow> {

    override fun compareTo(other: MeasurementWindow) = measurements.sum().compareTo(other.measurements.sum())

    fun add(measurement: Int) = measurements.add(measurement)
}

private fun part1(input: List<Int>) = input.asSequence().compareWithPrevious().count { it > 0 }

private fun part2(input: List<Int>): Int {
    val windows = 3
    val windowQueue: Queue<MeasurementWindow> = ArrayDeque(windows)
    val windowSequence = sequence {
        input.forEach { measurement ->
            // add measurement to all active windows
            windowQueue.forEach { it.add(measurement) }
            // special case to handle the first few measurements, where we don't have enough windows yet
            if (windowQueue.size == windows) {
                // remove & emit the first (full) window
                windowQueue.poll()?.run {
                    yield(this)
                }
            }
            // add new empty window at the end
            windowQueue.add(MeasurementWindow())
        }
    }
    return windowSequence.compareWithPrevious().count { it > 0 }
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test").map { it.toInt() }
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01").map { it.toInt() }
    println(part1(input))
    println(part2(input))
}
