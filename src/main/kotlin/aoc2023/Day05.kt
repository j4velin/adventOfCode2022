package aoc2023

import cut
import readInput
import separateBy

private data class MappingRange(val range: LongRange, val offset: Long)

private data class ConversionMap(val name: String, private val mappings: List<MappingRange>) {

    companion object {
        private val MAP_REGEX = """(?<destination>\d+)\s+(?<source>\d+)\s+(?<size>\d+)""".toRegex()
        fun fromStringList(input: List<String>): ConversionMap {
            val name = input.first().replace(" map:", "")
            val mappings = mutableListOf<MappingRange>()
            input.drop(1).forEach { line ->
                val result = MAP_REGEX.find(line)
                if (result != null) {
                    val size = result.groups["size"]!!.value.toInt() - 1
                    val sourceStart = result.groups["source"]!!.value.toLong()
                    val destinationStart = result.groups["destination"]!!.value.toLong()
                    mappings.add(
                        MappingRange(
                            LongRange(sourceStart, sourceStart + size),
                            destinationStart - sourceStart
                        )
                    )
                } else {
                    throw IllegalArgumentException("invalid input: $line")
                }
            }
            return ConversionMap(name, mappings.sortedBy { it.range.first })
        }
    }

    fun map(input: Long) = mappings.find { input in it.range }?.let { it.offset + input } ?: input

    fun map(input: LongRange): Sequence<LongRange> = sequence {
        var gaps = listOf(input)
        mappings.filter { it.range.last >= input.first && it.range.first <= input.last }.forEach {
            val overlapStart = it.range.first.coerceAtLeast(input.first)
            val overlapEnd = it.range.last.coerceAtMost(input.last)
            val overlap = LongRange(overlapStart, overlapEnd)
            yield(LongRange(overlapStart + it.offset, overlapEnd + it.offset))

            gaps = gaps.flatMap { gap -> gap.cut(overlap) }
        }

        if (gaps.isNotEmpty()) {
            gaps.forEach { yield(it) }
        }
    }
}

object Day05 {
    fun part1(input: List<String>): Long {
        val seeds = input.first().replace("seeds: ", "").split("\\s".toRegex()).map { it.toLong() }
        val maps = input.drop(2).separateBy { it.isEmpty() }.map { ConversionMap.fromStringList(it) }

        return seeds.minOf { seed ->
            var currentValue = seed
            maps.forEach { currentValue = it.map(currentValue) }
            currentValue
        }
    }

    fun part2(input: List<String>): Long {
        val regex = """(?<start>\d+)\s+(?<size>\d+)\s?""".toRegex()
        val seedRanges = regex.findAll(input.first()).map { result ->
            val start = result.groups["start"]!!.value.toLong()
            LongRange(start, start + result.groups["size"]!!.value.toLong() - 1L)
        }
        val maps = input.drop(2).separateBy { it.isEmpty() }.map { ConversionMap.fromStringList(it) }

        return seedRanges.minOf { seedRange ->
            var currentSequence = sequenceOf(seedRange)
            maps.forEach { conversionMap -> currentSequence = currentSequence.flatMap { conversionMap.map(it) } }
            currentSequence.minOf { it.first }
        }
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
