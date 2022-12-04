package aoc2021

import readInput

private enum class Direction { FORWARD, DOWN, UP }

private data class Movement(val direction: Direction, val distance: Int) {
    companion object {
        fun fromString(str: String): Movement {
            val split = str.split(" ")
            return Movement(Direction.valueOf(split[0].uppercase()), split[1].toInt())
        }
    }
}

private fun part1(input: List<String>): Int {
    var x = 0
    var y = 0
    input.map(Movement.Companion::fromString).forEach {
        when (it.direction) {
            Direction.FORWARD -> x += it.distance
            Direction.DOWN -> y += it.distance
            Direction.UP -> y -= it.distance
        }
    }
    return x * y
}

private fun part2(input: List<String>): Int {
    var x = 0
    var y = 0
    var aim = 0
    input.map(Movement.Companion::fromString).forEach {
        when (it.direction) {
            Direction.FORWARD -> {
                x += it.distance
                y += it.distance * aim
            }
            Direction.DOWN -> aim += it.distance
            Direction.UP -> aim -= it.distance
        }
    }
    return x * y
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
