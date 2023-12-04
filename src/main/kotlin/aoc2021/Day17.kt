package aoc2021

import Point
import readInput
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.sign

/**
 * A path is just a collection of points
 * @property points the points along this path
 */
@JvmInline
private value class Path(val points: List<Point>)

/**
 * @param vx the initial x velocity
 * @param vy the initial y velocity
 * @return a sequence of points when starting at (0,0) and the given initial accelerations
 */
private fun getPositions(vx: Int, vy: Int): Sequence<Point> {
    var currentVelocityX = vx
    var currentVelocityY = vy
    return generateSequence(Point(0, 0)) {
        val p = it.move(currentVelocityX, currentVelocityY)
        currentVelocityX -= currentVelocityX.sign
        currentVelocityY--
        p
    }
}

/**
 * @param input the input describing the position of the target area in the form of "target area: x=20..30, y=-10..-5"
 * @return all valid paths, which have at least one position within the given target area when starting at point (0,0)
 */
private fun getValidPaths(input: String): List<Path> {
    val area = input.drop("target area: x=".length).split(", y=").map { it.split("..") }
    val areaX = IntRange(area[0][0].toInt(), area[0][1].toInt())
    val areaY = IntRange(area[1][0].toInt(), area[1][1].toInt())

    // TODO: currently assumed that target area is on the bottom right somewhere
    // calculate initial x velocities, which result in an x coordinate within areaX at some point
    // example: x positions with velocity 5 after each step: 0, 5, 5+4, 5+4+3, 5+4+3+2, 5+4+3+2+1
    val validXAccelerations = IntRange(1, areaX.last).filter { initialVelocity ->
        var positionX = 0
        for (velocityX in initialVelocity downTo 0) {
            if (positionX in areaX) {
                return@filter true
            }
            positionX += velocityX
        }
        false
    }

    // same for y, but here we can shoot up- and downwards (or not shoot in y direction at all and just let it fall)
    val maxY = max(areaY.first.absoluteValue, areaY.last.absoluteValue)
    val validYAccelerations = IntRange(-maxY, maxY).filter { initialVelocity ->
        var positionY = 0
        var velocityY = initialVelocity
        while (positionY >= areaY.first) {
            if (positionY in areaY) {
                return@filter true
            }
            positionY += velocityY
            velocityY--
        }
        false
    }

    // build list of all paths
    return buildList {
        for (vx in validXAccelerations) {
            for (vy in validYAccelerations) {
                // generate positions as long as there is a chance to end up within the target area
                add(Path(getPositions(vx, vy).takeWhile { it.x <= areaX.last && it.y >= areaY.first }.toList()))
            }
        }
        // filter only those paths, where at least one position is within the target area (aka 'valid paths')
    }.filter { it.points.any { pos -> pos.x in areaX && pos.y in areaY } }
}

private fun part1(input: String) = getValidPaths(input).map { path -> path.points.maxOf { it.y } }.maxOf { it }

private fun part2(input: String) = getValidPaths(input).count()

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test").first()
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    val input = readInput("Day17").first()
    println(part1(input))
    println(part2(input))
}
