package aoc2022

import readInput

// input contains duplicates...
data class MixedNumber(val value: Number, val mixCount: Int)

private fun mixNumbers(original: LongArray, rounds: Int): List<Number> {
    val list = original.map { MixedNumber(it, 0) }.toMutableList()
    repeat(rounds) { round ->
        original.forEach { number ->
            val search = MixedNumber(number, round)
            val oldIndex = list.indexOf(search)
            list.remove(search)
            val newIndex = ((((oldIndex + number) % list.size) + list.size) % list.size).toInt()
            val newElement = MixedNumber(number, round + 1)
            list.add(newIndex, newElement)
        }
    }
    return list.map { it.value }
}

private fun part1(input: List<String>): Int {
    val array = input.map { it.toLong() }.toLongArray()
    val list = mixNumbers(array, 1)
    val startIndex = list.indexOfFirst { it == 0L }
    return list[(startIndex + 1000) % list.size].toInt() + list[(startIndex + 2000) % list.size].toInt() + list[(startIndex + 3000) % list.size].toInt()
}

private fun part2(input: List<String>): Long {
    val array = input.map { it.toLong() * 811589153L }.toLongArray()
    val list = mixNumbers(array, 10)
    val startIndex = list.indexOfFirst { it == 0L }
    return list[(startIndex + 1000) % list.size].toLong() + list[(startIndex + 2000) % list.size].toLong() + list[(startIndex + 3000) % list.size].toLong()
}

fun main() {
    val testInput = readInput("Day20_test", 2022)
    check(part1(testInput) == 3)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20", 2022)
    println(part1(input))
    println(part2(input))
}
