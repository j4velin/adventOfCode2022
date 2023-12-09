package aoc2023

import readInput

private class History(private val initialSequence: List<Int>) {

    companion object {
        fun fromString(line: String) = History(line.split(" ").map { char -> char.toInt() })
    }

    fun getNextValue(): Int {
        var currentSequence = initialSequence
        val lastValues = mutableListOf(currentSequence.last())
        while (currentSequence.sum() != 0) {
            currentSequence = getNextSequence(currentSequence)
            lastValues.add(currentSequence.last())
        }

        var currentPrediction = 0
        for (i in lastValues.size - 1 downTo 0) {
            currentPrediction += lastValues[i]
        }

        return currentPrediction
    }


    fun getPreviousValue(): Int {
        var currentSequence = initialSequence
        val firstValues = mutableListOf(currentSequence.first())
        while (currentSequence.sum() != 0) {
            currentSequence = getNextSequence(currentSequence)
            firstValues.add(currentSequence.first())
        }

        var currentPrediction = 0
        for (i in firstValues.size - 1 downTo 0) {
            currentPrediction = firstValues[i] - currentPrediction
        }

        return currentPrediction
    }

    private fun getNextSequence(input: List<Int>) = input.windowed(size = 2, step = 1) { it.last() - it.first() }
}


object Day09 {
    fun part1(input: List<String>) = input.map { History.fromString(it) }.sumOf { it.getNextValue() }

    fun part2(input: List<String>) = input.map { History.fromString(it) }.sumOf { it.getPreviousValue() }
}

fun main() {
    val testInput = readInput("Day09_test", 2023)
    check(Day09.part1(testInput) == 114)
    check(Day09.part2(testInput) == 2)

    val input = readInput("Day09", 2023)
    println(Day09.part1(input))
    println(Day09.part2(input))
}
