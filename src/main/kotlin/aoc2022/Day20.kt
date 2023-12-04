package aoc2022

import readInput

// input contains duplicates, so keep the original index
data class MixedNumber(val value: Long, val originalIndex: Int)

private fun mixNumbers(original: LongArray, rounds: Int): List<Long> {
    val list = original.withIndex().map { MixedNumber(it.value, it.index) }.toMutableList()
    repeat(rounds) {
        original.indices.forEach { originalIndex ->
            val oldIndex = list.indexOfFirst { it.originalIndex == originalIndex }
            val element = list.removeAt(oldIndex)
            val newIndex = ((((oldIndex + element.value) % list.size) + list.size) % list.size).toInt()
            list.add(newIndex, element)
        }
    }
    return list.map { it.value }
}

private fun part1(input: List<String>): Long {
    val array = input.map { it.toLong() }.toLongArray()
    val list = mixNumbers(array, 1)
    val startIndex = list.indexOfFirst { it == 0L }
    return list[(startIndex + 1000) % list.size] + list[(startIndex + 2000) % list.size] + list[(startIndex + 3000) % list.size]
}

private fun part2(input: List<String>): Long {
    val array = input.map { it.toLong() * 811589153L }.toLongArray()
    val list = mixNumbers(array, 10)
    val startIndex = list.indexOfFirst { it == 0L }
    return list[(startIndex + 1000) % list.size] + list[(startIndex + 2000) % list.size] + list[(startIndex + 3000) % list.size]
}

fun main() {
    val testInput = readInput("Day20_test", 2022)
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20", 2022)
    println(part1(input))
    println(part2(input))
}
