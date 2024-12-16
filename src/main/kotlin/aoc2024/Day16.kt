package aoc2024

import Direction
import PointL
import find
import get
import readInput
import to2dCharArray

object Day16 {

    private fun Array<CharArray>.findShortestPathCost(start: PointL, end: PointL): Long {
        val minimumDistances: MutableMap<Pair<PointL, Direction>, Long> = mutableMapOf((start to Direction.EAST) to 0L)
        val toVisit = mutableMapOf((start to Direction.EAST) to 0L)
        while (toVisit.isNotEmpty()) {
            val currentEntry = toVisit.minBy { it.value }.also { toVisit.remove(it.key) }
            if (currentEntry.key.first == end) {
                return currentEntry.value
            }
            val neighbours = currentEntry.key.first.getNeighbours().filter { this.get(it.x, it.y) != '#' }
            neighbours.forEach { neighbour ->
                var currentDirection = currentEntry.key.second
                repeat(4) { rotations ->
                    val rotationCost = (if (rotations == 3) 1 else rotations) * 1000
                    if (currentEntry.key.first + currentDirection.delta == neighbour) {
                        val currentMin = minimumDistances[neighbour to currentDirection] ?: Long.MAX_VALUE
                        val newDistance = currentEntry.value + 1 + rotationCost
                        if (newDistance < currentMin) {
                            val newValue = currentEntry.value + 1 + rotationCost
                            minimumDistances[neighbour to currentDirection] = newValue
                            toVisit[neighbour to currentDirection] = newValue
                        }
                    }
                    currentDirection = currentDirection.rotateClockwise()
                }
            }
        }
        return Long.MAX_VALUE
    }

    private fun Array<CharArray>.findShortestPathsPoints(start: PointL, end: PointL): List<PointL> {
        val minimumDistances: MutableMap<Pair<PointL, Direction>, Pair<Long, List<PointL>>> =
            mutableMapOf((start to Direction.EAST) to (0L to listOf(start)))
        val toVisit = mutableMapOf((start to Direction.EAST) to (0L to listOf(start)))
        while (toVisit.isNotEmpty()) {
            val currentEntry = toVisit.minBy { it.value.first }.also { toVisit.remove(it.key) }
            if (currentEntry.key.first == end) {
                return currentEntry.value.second.distinct()
            }
            val neighbours = currentEntry.key.first.getNeighbours().filter { this.get(it.x, it.y) != '#' }
            neighbours.forEach { neighbour ->
                var currentDirection = currentEntry.key.second
                repeat(4) { rotations ->
                    val rotationCost = (if (rotations == 3) 1 else rotations) * 1000
                    if (currentEntry.key.first + currentDirection.delta == neighbour) {
                        val currentMin = minimumDistances[neighbour to currentDirection]?.first ?: Long.MAX_VALUE
                        val newDistance = currentEntry.value.first + 1 + rotationCost
                        if (newDistance < currentMin) {
                            val newValue =
                                currentEntry.value.first + 1 + rotationCost to currentEntry.value.second + neighbour
                            minimumDistances[neighbour to currentDirection] = newValue
                            toVisit[neighbour to currentDirection] = newValue
                        } else if (newDistance == currentMin) {
                            // two paths possible
                            val newValue =
                                currentMin to minimumDistances[neighbour to currentDirection]!!.second + currentEntry.value.second + neighbour
                            minimumDistances[neighbour to currentDirection] = newValue
                            toVisit[neighbour to currentDirection] = newValue
                        }
                    }
                    currentDirection = currentDirection.rotateClockwise()
                }
            }
        }
        return emptyList()
    }

    fun part1(input: List<String>): Int {
        val map = input.to2dCharArray()
        val start = map.find('S') ?: throw IllegalStateException("start not found")
        val end = map.find('E') ?: throw IllegalStateException("end not found")
        return map.findShortestPathCost(start, end).toInt()
    }

    fun part2(input: List<String>): Int {
        val map = input.to2dCharArray()
        val start = map.find('S') ?: throw IllegalStateException("start not found")
        val end = map.find('E') ?: throw IllegalStateException("end not found")
        return map.findShortestPathsPoints(start, end).size
    }
}

fun main() {
    val testInput = readInput("Day16_test", 2024)
    check(Day16.part1(testInput) == 11048)
    check(Day16.part2(testInput) == 64)

    val input = readInput("Day16", 2024)
    println(Day16.part1(input))
    println(Day16.part2(input))
}
