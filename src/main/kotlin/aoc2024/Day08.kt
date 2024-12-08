package aoc2024

import PointL
import allDigits
import allLetters
import findAll
import grid
import readInput
import to2dCharArray
import withEachOf

object Day08 {

    private data class Antenna(val position: PointL, val frequency: Char)

    fun part1(input: List<String>): Int {
        return getUniqueAntinodePositions(input, withResonance = false).count()
    }

    fun part2(input: List<String>): Int {
        return getUniqueAntinodePositions(input, withResonance = true).count()
    }

    private fun getUniqueAntinodePositions(input: List<String>, withResonance: Boolean = false): Set<PointL> {
        val map = input.to2dCharArray()
        val antennasByFrequency = map.findAll("${allDigits}${allLetters}${allLetters.uppercase()}".toCharArray())
            .map { Antenna(it.key, it.value) }.groupBy { it.frequency }

        val antinodePositions = buildSet {
            antennasByFrequency.forEach { entry ->
                val antennas = entry.value.asSequence()
                antennas.withEachOf(antennas).filter { pair -> pair.first != pair.second }
                    .forEach { (antenna1, antenna2) ->
                        val delta =
                            PointL(antenna1.position.x - antenna2.position.x, antenna1.position.y - antenna2.position.y)

                        val sequence = if (withResonance) {
                            generateSequence(0) { it + 1 }
                        } else {
                            sequenceOf(1)
                        }

                        sequence.map { antenna1.position + delta * it }
                            .takeWhile { it.isWithin(map.grid) }
                            .forEach { add(it) }

                        sequence.map { antenna2.position - delta * it }
                            .takeWhile { it.isWithin(map.grid) }
                            .forEach { add(it) }
                    }
            }
        }
        return antinodePositions
    }
}

fun main() {
    val testInput = readInput("Day08_test", 2024)
    check(Day08.part1(testInput) == 14)
    check(Day08.part2(testInput) == 34)

    val input = readInput("Day08", 2024)
    println(Day08.part1(input))
    println(Day08.part2(input))
}
