package aoc2021

import Point
import readInput
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.sign

/**
 * Creates a new [Point] by moving this instance 1 step in the direction of the given [end] point
 *
 * @param end the destination in which direction to move the new point
 * @return a new point instance which is moved 1 step in the direction of [end]
 */
fun Point.moveTo(end: Point): Point {
    val nextX = x + (end.x - x).sign
    val nextY = y + (end.y - y).sign
    return Point(nextX, nextY)
}

private data class Line(val start: Point, val end: Point) : Iterable<Point> {

    val isHorizontal = start.x == end.x
    val isVertical = start.y == end.y
    val isDiagonal = (start.x - end.x).absoluteValue == (start.y - end.y).absoluteValue

    companion object {
        fun fromString(data: String) =
            data.split(" -> ").map { point -> point.split(",").map { it.toInt() } }.map { Point(it[0], it[1]) }
                .let { Line(it[0], it[1]) }
    }

    override fun iterator(): Iterator<Point> {
        return object : Iterator<Point> {
            private var currentPosition: Point? = null
            override fun hasNext() = currentPosition != end

            override fun next(): Point {
                currentPosition = currentPosition?.moveTo(end) ?: start
                return currentPosition ?: start
            }
        }
    }
}

/**
 * Gets the most dangerous points along the given lines.
 *
 * @param lines the lines covering all the points to consider
 * @param dangerousLimit the threshold, at which a point is considered to be 'dangerous'
 * @return a set of points, which are covered by at least [dangerousLimit] lines of [lines]
 */
private fun getMostDangerousPoints(lines: List<Line>, dangerousLimit: Int): Set<Point> {
    val maxX = lines.maxOf { max(it.start.x, it.end.x) } + 1
    val maxY = lines.maxOf { max(it.start.y, it.end.y) } + 1
    val diagram = Array(maxX) { IntArray(maxY) }
    val mostDangerousPoints = mutableSetOf<Point>()
    lines.flatten().forEach {
        if (++diagram[it.x][it.y] >= dangerousLimit) {
            mostDangerousPoints.add(it)
        }
    }
    return mostDangerousPoints
}

private fun part1(input: List<String>): Int {
    val lines = input.map(Line.Companion::fromString).filter { it.isHorizontal || it.isVertical }
    return getMostDangerousPoints(lines, 2).size
}

private fun part2(input: List<String>): Int {
    val lines = input.map(Line.Companion::fromString).filter { it.isHorizontal || it.isVertical || it.isDiagonal }
    return getMostDangerousPoints(lines, 2).size
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
