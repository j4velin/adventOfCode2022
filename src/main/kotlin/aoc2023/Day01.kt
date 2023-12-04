package aoc2023

import readInput

object Day01 {
    fun part1(input: List<String>): Int {
        return input.map { "${it.filter { c -> c.isDigit() }.take(1)}${it.filter { c -> c.isDigit() }.takeLast(1)}" }
            .sumOf { it.toInt() }
    }

    fun part2(input: List<String>): Int {
        val replacements = mapOf(
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "seven" to "7",
            "eight" to "8",
            "nine" to "9",
        )
        val list: List<String> = input.map { line ->
            var newLine = line
            // keep the original text before and after the replacement to account for overlapping texts: eightwo
            replacements.forEach { newLine = newLine.replace(it.key, "${it.key}${it.value}${it.key}") }
            newLine
        }
        return part1(list)
    }
}

fun main() {
    val testInput = readInput("Day01_test", 2023)
    check(Day01.part1(testInput) == 142)
    val testInput2 = readInput("Day01_test_2", 2023)
    check(Day01.part2(testInput2) == 281)

    val input = readInput("Day01", 2023)
    println(Day01.part1(input))
    println(Day01.part2(input))
}
