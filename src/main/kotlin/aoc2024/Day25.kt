package aoc2024

import readInput
import separateBy

object Day25 {

    private const val MAX_HEIGHT = 7

    private class Lock(val heights: IntArray) {
        infix fun fits(key: Key) =
            heights.withIndex().all { (index, value) -> key.heights[index] + value <= MAX_HEIGHT }
    }

    private class Key(val heights: IntArray)

    fun part1(input: List<String>): Int {
        val (locks, keys) = input.separateBy { it.isEmpty() }.map {
            val heights = IntArray(it.first().length)
            for (x in 0..<it.first().length) {
                heights[x] = it.count { row -> row[x] == '#' }
            }
            if (it.first().contains("#")) {
                Lock(heights)
            } else {
                Key(heights)
            }
        }.partition { it is Lock }

        return locks.map { it as Lock }.sumOf { lock -> keys.map { it as Key }.count { key -> lock fits key } }
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day25_test", 2024)
    check(Day25.part1(testInput) == 3)
    check(Day25.part2(testInput) == 0)

    val input = readInput("Day25", 2024)
    println(Day25.part1(input))
    println(Day25.part2(input))
}
