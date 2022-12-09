package aoc2022

import Point
import readInput
import kotlin.math.sign

fun main() {

    /**
     * Moves a rope along the given movements
     *
     * @param input the movement input
     * @param knotSize the number of knots in this rope
     * @return the unique positions the last knot visited during the rope movement
     */
    fun moveRope(input: List<String>, knotSize: Int): Int {
        val start = Point(0, 0)
        val knots = Array(knotSize) { start }
        val visitedPoints = mutableSetOf(start)
        input.map { it.split(" ") }.forEach { (direction, distance) ->
            repeat(distance.toInt()) {
                knots[0] = when (direction) {
                    "U" -> knots[0].move(0, 1)
                    "D" -> knots[0].move(0, -1)
                    "L" -> knots[0].move(-1, 0)
                    "R" -> knots[0].move(1, 0)
                    else -> throw IllegalArgumentException("Unknown direction: $direction")
                }
                for (i in knots.indices.drop(1)) {
                    val isLastKnot = i == knots.size - 1
                    val previous = knots[i - 1]
                    val neighbours = previous.getNeighbours(true)
                    var knot = knots[i]
                    while (previous != knot && !neighbours.contains(knot)) {
                        knot = knot.move((previous.x - knot.x).sign, (previous.y - knot.y).sign)
                        if (isLastKnot) {
                            visitedPoints.add(knot)
                        }
                    }
                    knots[i] = knot
                }
            }
        }
        return visitedPoints.size
    }

    fun part1(input: List<String>) = moveRope(input, 2)

    fun part2(input: List<String>) = moveRope(input, 10)

    val testInput = readInput("Day09_test", 2022)
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)
    val testInput2 = """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
    """.trimIndent().split("\n")
    check(part2(testInput2) == 36)

    val input = readInput("Day09", 2022)
    println(part1(input))
    println(part2(input))
}