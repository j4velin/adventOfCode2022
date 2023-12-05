package aoc2023

import readInput
import separateBy

private data class ConversionMap(val name: String, val sources: List<LongRange>, val offsets: List<Long>) {
    companion object {
        private val MAP_REGEX = """(?<destination>\d+)\s+(?<source>\d+)\s+(?<size>\d+)""".toRegex()
        fun fromStringList(input: List<String>): ConversionMap {
            val name = input.first().replace(" map:", "")
            val sources = mutableListOf<LongRange>()
            val offsets = mutableListOf<Long>()
            input.drop(1).forEach { line ->
                val result = MAP_REGEX.find(line)
                if (result != null) {
                    val size = result.groups["size"]!!.value.toInt() - 1
                    val sourceStart = result.groups["source"]!!.value.toLong()
                    val destinationStart = result.groups["destination"]!!.value.toLong()
                    sources.add(LongRange(sourceStart, sourceStart + size))
                    offsets.add(destinationStart - sourceStart)
                } else {
                    throw IllegalArgumentException("invalid input: $line")
                }
            }
            return ConversionMap(name, sources, offsets)
        }
    }

    fun map(input: Long) = sources.withIndex().find { input in it.value }?.let { (idx, _) ->
        offsets[idx] + input
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
        val regex = """(?<start>\d+)\s+(?<size>\d+)\s?""".toRegex()
        val seedRanges = regex.findAll(input.first()).map { result ->
            val start = result.groups["start"]!!.value.toLong()
            LongRange(start, start + result.groups["size"]!!.value.toLong() - 1L)
        }
        // TODO

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
