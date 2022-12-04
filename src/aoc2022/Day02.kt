package aoc2022

import readInput
import java.lang.IllegalArgumentException

enum class Shape(val value: Int) {
    ROCK(1), PAPER(2), SCISSORS(3);

    companion object {
        fun fromChar(char: Char) = when (char) {
            'A', 'X' -> ROCK
            'B', 'Y' -> PAPER
            'C', 'Z' -> SCISSORS
            else -> throw IllegalArgumentException("Unknown shape: $char")
        }
    }
}

enum class Result(val value: Int) {
    WIN(6), DRAW(3), LOSS(0);

    companion object {
        fun fromChar(char: Char) = when (char) {
            'X' -> LOSS
            'Y' -> DRAW
            'Z' -> WIN
            else -> throw IllegalArgumentException("Unknown result: $char")
        }
    }
}

fun main() {

    fun play(opponent: Shape, myself: Shape) = when {
        opponent == Shape.ROCK && myself == Shape.PAPER -> Result.WIN
        opponent == Shape.ROCK && myself == Shape.SCISSORS -> Result.LOSS
        opponent == Shape.PAPER && myself == Shape.ROCK -> Result.LOSS
        opponent == Shape.PAPER && myself == Shape.SCISSORS -> Result.WIN
        opponent == Shape.SCISSORS && myself == Shape.ROCK -> Result.WIN
        opponent == Shape.SCISSORS && myself == Shape.PAPER -> Result.LOSS
        else -> Result.DRAW
    }

    fun findShape(opponent: Shape, expectedResult: Result) = when {
        opponent == Shape.ROCK && expectedResult == Result.WIN -> Shape.PAPER
        opponent == Shape.ROCK && expectedResult == Result.LOSS -> Shape.SCISSORS
        opponent == Shape.PAPER && expectedResult == Result.WIN -> Shape.SCISSORS
        opponent == Shape.PAPER && expectedResult == Result.LOSS -> Shape.ROCK
        opponent == Shape.SCISSORS && expectedResult == Result.WIN -> Shape.ROCK
        opponent == Shape.SCISSORS && expectedResult == Result.LOSS -> Shape.PAPER
        else -> opponent // draw
    }

    fun part1(input: List<String>): Int {
        val rounds = input.map {
            val (p0, p1) = it.split(" ", limit = 2).map { str -> Shape.fromChar(str.toCharArray().first()) }
            val result = play(p0, p1)
            result.value + p1.value
        }
        return rounds.sum()
    }

    fun part2(input: List<String>): Int {
        val rounds = input.map {
            val round = it.split(" ", limit = 2)
            val opponentShape = Shape.fromChar(round[0].toCharArray().first())
            val expectedResult = Result.fromChar(round[1].toCharArray().first())
            val myShape = findShape(opponentShape, expectedResult)
            expectedResult.value + myShape.value
        }
        return rounds.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
