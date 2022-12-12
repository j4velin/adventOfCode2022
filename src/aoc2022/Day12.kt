package aoc2022

import Point
import readInput

private class Input(val map: Array<IntArray>, val start: Point, val end: Point)

private fun parseInput(input: List<String>): Input {
    lateinit var start: Point
    lateinit var end: Point
    val map = input.withIndex().map { row ->
        row.value.toCharArray().withIndex().map { column ->
            when (column.value) {
                'S' -> {
                    start = Point(column.index, row.index)
                    0
                }

                'E' -> {
                    end = Point(column.index, row.index)
                    'z' - 'a'
                }

                else -> column.value - 'a'
            }
        }.toIntArray()
    }.toTypedArray()
    return Input(map, start, end)
}

/**
 * Finds the minimal distance from [current] to a point satisfying the [endCheck]
 *
 * @param current the starting point
 * @param pathLength the path length so far
 * @param minDistanceMap a map with the current minimal distances from the original start
 * @param reachableNeighbours a function returning all reachable neighbours of a given point
 * @param endCheck a check to determine if we reached our end
 *
 * @return the minimum distance to the first point satisfying the [endCheck]
 */
private fun findMinDistance(
    current: Point,
    pathLength: Int,
    minDistanceMap: Array<IntArray>,
    reachableNeighbours: (Point) -> List<Point>,
    endCheck: (Point) -> Boolean = { false }
): Int {
    val pointsToVisit = mutableSetOf<Point>()
    for (p in reachableNeighbours(current)) {
        if (minDistanceMap[p.y][p.x] > pathLength + 1) {
            minDistanceMap[p.y][p.x] = pathLength + 1
            if (endCheck(p)) {
                return pathLength + 1
            }
            pointsToVisit.add(p)
        }
    }
    if (pointsToVisit.isEmpty()) return Integer.MAX_VALUE // dead end
    return pointsToVisit.minOf { findMinDistance(it, pathLength + 1, minDistanceMap, reachableNeighbours, endCheck) }
}

private fun part1(input: List<String>): Int {
    val i = parseInput(input)

    val minDistanceMap = Array(i.map.size) { IntArray(i.map.first().size) { Int.MAX_VALUE } }
    minDistanceMap[i.start.y][i.start.x] = 0
    val reachableNeighbours = { current: Point ->
        val neighbours = current.getNeighbours(validGrid = Point(0, 0) to Point(i.map.first().size - 1, i.map.size - 1))
        val currentElevation = i.map[current.y][current.x]
        neighbours.filter { currentElevation >= i.map[it.y][it.x] - 1 }
    }
    val endCheck = { point: Point -> point == i.end }
    return findMinDistance(i.start, 0, minDistanceMap, reachableNeighbours, endCheck)
}

private fun part2(input: List<String>): Int {
    val i = parseInput(input)

    val minDistanceMap = Array(i.map.size) { IntArray(i.map.first().size) { Int.MAX_VALUE } }
    minDistanceMap[i.end.y][i.end.x] = 0
    val reachableNeighbours = { current: Point ->
        val neighbours = current.getNeighbours(validGrid = Point(0, 0) to Point(i.map.first().size - 1, i.map.size - 1))
        val currentElevation = i.map[current.y][current.x]
        neighbours.filter { currentElevation <= i.map[it.y][it.x] + 1 }
    }
    val endCheck = { point: Point -> i.map[point.y][point.x] == 0 }
    return findMinDistance(i.end, 0, minDistanceMap, reachableNeighbours, endCheck)
}

fun main() {
    val testInput = readInput("Day12_test", 2022)
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12", 2022)
    println(part1(input))
    println(part2(input))
}
