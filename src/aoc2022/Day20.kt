package aoc2022

import readInput

// input contains duplicates...
data class MixedNumber(val value: Number, val mixCount: Int)

private fun part1(input: List<String>): Int {
    val array = input.map { it.toInt() }.toIntArray()
    val list = array.map { MixedNumber(it, 0) }.toMutableList()
    array.forEach { number ->
        val search = MixedNumber(number, 0)
        val new = MixedNumber(number, search.mixCount + 1)
        val oldIndex = list.indexOf(search)
        list.remove(search)
        val newIndex = (oldIndex + number) % list.size
        when {
            newIndex >= 0 -> list.add(newIndex, new)
            else -> list.add((newIndex + list.size) % list.size, new)
        }
    }
    val startIndex = list.indexOfFirst { it.value == 0 }
    return list[(startIndex + 1000) % list.size].value.toInt() + list[(startIndex + 2000) % list.size].value.toInt() + list[(startIndex + 3000) % list.size].value.toInt()
}

private fun part2(input: List<String>): Long {
    val array = input.map { it.toLong() * 811589153L }.toLongArray()
    val list = array.map { MixedNumber(it, 0) }.toMutableList()
    repeat(10) { round ->
        array.forEach { number ->
            val search = MixedNumber(number, round)
            val new = MixedNumber(number, search.mixCount + 1)
            val oldIndex = list.indexOf(search)
            list.remove(search)
            val newIndex = ((oldIndex + number) % list.size).toInt()
            when {
                newIndex >= 0 -> list.add(newIndex, new)
                else -> list.add((newIndex + list.size) % list.size, new)
            }
        }
    }
    val startIndex = list.indexOfFirst { it.value == 0L }
    return list[(startIndex + 1000) % list.size].value.toLong() + list[(startIndex + 2000) % list.size].value.toLong() + list[(startIndex + 3000) % list.size].value.toLong()
}

fun main() {
    val testInput = readInput("Day20_test", 2022)
    check(part1(testInput) == 3)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20", 2022)
    println(part1(input))
    println(part2(input))
}
