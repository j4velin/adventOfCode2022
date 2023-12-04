package aoc2022

import Point
import readInput
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class SensorBeaconPair(val sensor: Point, val beacon: Point) {
    val hammingDistance = abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)

    companion object {
        fun fromString(input: String): SensorBeaconPair {
            val split = input.drop("Sensor at x=".length).split(": closest beacon is at x=")
            val (sensorX, sensorY) = split[0].split(", y=").map { value -> value.toInt() }
            val (beaconX, beaconY) = split[1].split(", y=").map { value -> value.toInt() }
            return SensorBeaconPair(Point(sensorX, sensorY), Point(beaconX, beaconY))
        }
    }
}

private fun part1(input: List<String>, row: Int): Int {
    // Sensor at x=2, y=18: closest beacon is at x=-2, y=15
    val allPairs = input.map { SensorBeaconPair.fromString(it) }.filter {
        row in it.sensor.y - it.hammingDistance..it.sensor.y + it.hammingDistance
    }
    val coveredPoints = mutableSetOf<Int>()
    allPairs.forEach {
        val distanceToTargetRow = abs(it.sensor.y - row)
        val remainingHammingValue = it.hammingDistance - distanceToTargetRow
        for (x in it.sensor.x - remainingHammingValue..it.sensor.x + remainingHammingValue) {
            coveredPoints.add(x)
        }
    }
    val beaconsInRow = allPairs.map { it.beacon }.distinct().count { it.y == row }
    val sensorsInRow = allPairs.map { it.sensor }.distinct().count { it.y == row }
    return coveredPoints.size - beaconsInRow - sensorsInRow
}

private fun part2(input: List<String>, maxValue: Int): Long {
    val allPairs = input.map { SensorBeaconPair.fromString(it) }
    for (row in 0..maxValue) {
        if (row % 100000 == 0) {
            print(".")
        }
        val coveredPoints = BitSet(maxValue + 1)
        allPairs.forEach {
            val distanceToTargetRow = abs(it.sensor.y - row)
            val remainingHammingValue = it.hammingDistance - distanceToTargetRow
            if (it.beacon.y == row && it.beacon.x >= 0 && it.beacon.x <= maxValue) {
                coveredPoints.set(it.beacon.x)
            }

            if (it.sensor.y == row && it.sensor.x >= 0 && it.sensor.x <= maxValue) {
                coveredPoints.set(it.sensor.x)
            }

            if (remainingHammingValue > 0) {
                val range =
                    max(0, it.sensor.x - remainingHammingValue)..min(maxValue, it.sensor.x + remainingHammingValue)
                coveredPoints.set(range.first, range.last + 1)
            }
        }
        if (coveredPoints.cardinality() <= maxValue) {
            for (x in 0..maxValue) {
                if (!coveredPoints[x]) {
                    val signalPosition = Point(x, row)
                    println("\nSignal found at $signalPosition -> ${signalPosition.x * 4000000L + signalPosition.y}")
                    return signalPosition.x * 4000000L + signalPosition.y
                }
            }
        }
    }
    throw IllegalArgumentException("No distress signal found")
}

fun main() {
    val testInput = readInput("Day15_test", 2022)
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 20) == 56000011L)

    val input = readInput("Day15", 2022)
    println(part1(input, 2000000))
    println(part2(input, 4000000))
}
