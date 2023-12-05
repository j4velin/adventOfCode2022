package aoc2023

import readInput
import separateBy

private data class ConversionMap(val name: String, val source: List<LongRange>, val destination: List<LongRange>) {
    companion object {
        private val map_regex = """(?<destination>\d+)\s+(?<source>\d+)\s+(?<size>\d+)""".toRegex()
        fun fromStringList(input: List<String>): ConversionMap {
            val name = input.first().replace(" map:", "")
            val source = mutableListOf<LongRange>()
            val destination = mutableListOf<LongRange>()
            input.drop(1).forEach { line ->
                val result = map_regex.find(line)
                if (result != null) {
                    val size = result.groups["size"]!!.value.toInt() - 1
                    val sourceStart = result.groups["source"]!!.value.toLong()
                    val destinationStart = result.groups["destination"]!!.value.toLong()
                    source.add(LongRange(sourceStart, sourceStart + size))
                    destination.add(LongRange(destinationStart, destinationStart + size))
                } else {
                    throw IllegalArgumentException("invalid input: $line")
                }
            }
            return ConversionMap(name, source, destination)
        }
    }

    fun map(input: Long) = source.withIndex().find { input in it.value }?.let { (idx, range) ->
        val diff = range.indexOf(input)
        destination[idx].first + diff
    } ?: input
}

object Day05 {
    fun part1(input: List<String>): Long {
        val seeds = input.first().replace("seeds: ", "").split("\\s".toRegex()).map { it.toLong() }
        val maps = input.drop(2).separateBy { it.isEmpty() }.map { ConversionMap.fromStringList(it) }

        return seeds.minOf { seed ->
            var currentValue = seed
            maps.forEach { currentValue = it.map(currentValue) }
            println("seed: $seed -> location: $currentValue")
            currentValue
        }
    }

    fun part2(input: List<String>): Long {
        return 0L
    }
}

fun main() {
    val testInput = readInput("Day05_test", 2023)
    check(Day05.part1(testInput) == 35L)
    check(Day05.part2(testInput) == 46L)

    val input = readInput("Day05", 2023)
    println(Day05.part1(input))
    println(Day05.part2(input))
}
