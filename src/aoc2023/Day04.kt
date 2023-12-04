package aoc2023

import readInput
import java.util.regex.Pattern
import kotlin.math.pow

private data class Card(val winningNumbers: Set<Int>, val cardNumbers: Set<Int>) {
    val matches = winningNumbers.intersect(cardNumbers)
    val worth = if (matches.isEmpty()) 0 else 2f.pow(matches.size - 1).toInt()
}

object Day04 {
    fun part1(input: List<String>): Int {
        val regex = Pattern.compile("""Card\s+\d+: (?<winning>[\d\s]+) \| (?<card>[\d\s]+)""")
        return input.map { regex.matcher(it) }.filter { it.matches() }.sumOf { match ->
            val winningNumbers = match.group("winning").split(' ').filter { it.isNotBlank() }.map { it.toInt() }.toSet()
            val cardNumbers = match.group("card").split(' ').filter { it.isNotBlank() }.map { it.toInt() }.toSet()
            Card(winningNumbers, cardNumbers).worth
        }
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day04_test", 2023)
    check(Day04.part1(testInput) == 13)
    check(Day04.part2(testInput) == 0)

    val input = readInput("Day04", 2023)
    println(Day04.part1(input))
    println(Day04.part2(input))
}
