package aoc2024

import PointL
import readInput

object Day14 {

    private data class Robot(val start: PointL, val velocity: PointL, var currentPosition: PointL = start) {

        companion object {
            private val regex = """p=(?<startx>.*),(?<starty>.*) v=(?<vx>.*),(?<vy>.*)""".toRegex()

            fun fromString(input: String): Robot {
                val result = regex.find(input)
                if (result != null) {
                    return Robot(
                        start = PointL(
                            result.groups.get("startx")!!.value.toLong(),
                            result.groups.get("starty")!!.value.toLong()
                        ),
                        velocity = PointL(
                            result.groups.get("vx")!!.value.toLong(),
                            result.groups.get("vy")!!.value.toLong()
                        )
                    )
                } else {
                    throw IllegalArgumentException("Does not match regex: $input")
                }
            }
        }

        fun moveFromStart(seconds: Int, mapSize: PointL) {
            val delta = velocity * seconds
            currentPosition = (start + delta) % mapSize
        }

        fun move(mapSize: PointL) {
            currentPosition = (currentPosition + velocity) % mapSize
        }
    }

    fun part1(input: List<String>, mapSize: PointL): Int {
        val robots = input.map { Robot.fromString(it) }
        val finalPositions = robots.map {
            it.moveFromStart(100, mapSize)
            it.currentPosition
        }

        val middleX = mapSize.x / 2
        val middleY = mapSize.y / 2

        val (left, right) = finalPositions.filter { it.x != middleX && it.y != middleY }.partition { it.x < middleX }

        val (topLeft, bottomLeft) = left.partition { it.y < middleY }
        val (topRight, bottomRight) = right.partition { it.y < middleY }

        return topLeft.size * topRight.size * bottomLeft.size * bottomRight.size
    }

    private fun print(robots: List<Robot>, mapSize: PointL) {
        val positions = robots.map { it.currentPosition }
        for (y in 0..mapSize.y) {
            for (x in 0..mapSize.x) {
                val current = PointL(x, y)
                when {
                    x == mapSize.x / 2 -> print("|")
                    positions.contains(current) -> print("x")
                    else -> print(" ")
                }
            }
            println()
        }
    }

    fun part2(input: List<String>, mapSize: PointL): Int {
        val robots = input.map { Robot.fromString(it) }
        var steps = 0
        var keepSearching = true
        while (keepSearching) {
            steps++
            robots.forEach { it.move(mapSize) }

            // tree apparently is not centered, so checking if left and right halves are approximately mirrored did not work
            // --> check if there is any point which has "all" its neighbours and visually verify whether that forms some
            // tree or not
            val positions = robots.map { it.currentPosition }
            if (positions.any { p ->
                    p.getNeighbours(withDiagonal = true).filter { n -> positions.contains(n) }.size == 8
                }) {
                println()
                print(robots, mapSize)
                println()
                println("Tree found? [y/n]")
                keepSearching = readln() == "n"
            }
        }
        return steps
    }
}

fun main() {
    val testInput = readInput("Day14_test", 2024)
    check(Day14.part1(testInput, PointL(11, 7)) == 12)

    val input = readInput("Day14", 2024)
    println(Day14.part1(input, PointL(101, 103)))
    println(Day14.part2(input, PointL(101, 103)))
}
