package aoc2023

import readInput
import java.util.*

private enum class Color { RED, GREEN, BLUE }

private data class Cube(val amount: Int, val color: Color)

private data class Round(val cubes: Set<Cube>)

private data class Game(val id: Int, val rounds: List<Round>) {
    companion object {
        fun fromString(str: String): Game {
            val split = str.split(": ")
            val rounds = split[1].split("; ").map { round ->
                round.split(", ").map { cubes ->
                    val cubesSplit = cubes.split(" ")
                    Cube(cubesSplit[0].toInt(), Color.valueOf(cubesSplit[1].uppercase(Locale.getDefault())))
                }.toSet()
            }.map { Round(it) }
            return Game(split[0].replace("Game ", "").toInt(), rounds)
        }
    }
}

object Day02 {
    fun part1(input: List<String>): Int {
        // only 12 red cubes, 13 green cubes, and 14 blue cubes
        val isGamePossible = { game: Game ->
            game.rounds.all { round ->
                round.cubes.all { cube ->
                    when (cube.color) {
                        Color.RED -> cube.amount <= 12
                        Color.GREEN -> cube.amount <= 13
                        Color.BLUE -> cube.amount <= 14
                    }
                }
            }

        }
        return input.map { Game.fromString(it) }.filter { isGamePossible(it) }.sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        val getMaxAmountOfCubes = { game: Game, color: Color ->
            game.rounds.flatMap { round ->
                round.cubes.filter { it.color == color }.map { it.amount }
            }.max()
        }
        val getMinimumSet = { game: Game ->
            val red = getMaxAmountOfCubes(game, Color.RED)
            val green = getMaxAmountOfCubes(game, Color.GREEN)
            val blue = getMaxAmountOfCubes(game, Color.BLUE)
            setOf(Cube(red, Color.RED), Cube(green, Color.GREEN), Cube(blue, Color.BLUE))
        }
        return input.map { Game.fromString(it) }.map { getMinimumSet(it) }.sumOf { minSet ->
            var power = 1
            minSet.map { it.amount }.forEach { power *= it }
            power
        }
    }
}

fun main() {
    val testInput = readInput("Day02_test", 2023)
    check(Day02.part1(testInput) == 8)
    check(Day02.part2(testInput) == 2286)

    val input = readInput("Day02", 2023)
    println(Day02.part1(input))
    println(Day02.part2(input))
}
