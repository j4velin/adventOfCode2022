package aoc2023

import PointL
import readInput
import to2dCharArray
import java.util.*

object Day23 {

    private class HikingMap(input: List<String>) {
        private val map = input.to2dCharArray()
        private val start = PointL(
            x = map.withIndex().find { (x, column) -> column.first() == '.' }?.index
                ?: throw IllegalArgumentException("No start point found"),
            y = 0
        )
        private val end = PointL(
            x = map.withIndex().find { (x, column) -> column.last() == '.' }?.index
                ?: throw IllegalArgumentException("No end point found"),
            y = map.first().size - 1
        )
        private val grid = PointL(0, 0) to PointL(map.size - 1, map.first().size - 1)

        companion object {
            private val dRight = PointL(1, 0)
            private val dLeft = PointL(-1, 0)
            private val dTop = PointL(0, -1)
            private val dBottom = PointL(0, 1)
        }

        private val longestDistances = mutableMapOf(start to 0)
        private val longestDistancesWithComingFrom = mutableMapOf((start to start) to 0)

        val longestDistance by lazy { getLongestDistance(end, start, 0, setOf(start)) }
        val longestDistanceWithoutIce by lazy {
            val toVisit: Deque<Pair<PointL, Set<PointL>>> = LinkedList()
            toVisit.add(start to emptySet())

            while (toVisit.isNotEmpty()) {
                val (currentPoint, currentPath) = toVisit.poll()
                currentPoint.getNeighbours(validGrid = grid)
                    .filter { map[it.x.toInt()][it.y.toInt()] != '#' }
                    .filter { it !in currentPath }
                    .filter { next ->
                        val isTunnel = next.getNeighbours(validGrid = grid)
                            .filter { map[it.x.toInt()][it.y.toInt()] != '#' }.size == 2
                        if (isTunnel) {
                            currentPath.size + 1 > (longestDistancesWithComingFrom[currentPoint to next] ?: 0)
                        } else {
                            true
                        }
                    }
                    .forEach { next ->
                        val newPath = currentPath + next
                        toVisit.add(next to newPath)
                        longestDistancesWithComingFrom[currentPoint to next] = newPath.size
                        if (next == end) {
                            println(newPath.size)
                        }
                    }
            }

            longestDistancesWithComingFrom.filterKeys { it.second == end }.maxOf { it.value }
        }

        private fun getLongestDistance(
            target: PointL,
            current: PointL,
            stepCount: Int,
            visited: Set<PointL>
        ): Int {
            if (stepCount < (longestDistances[current] ?: 0)) {
                return 0
            } else {
                longestDistances[current] = stepCount
            }
            val next = when (map[current.x.toInt()][current.y.toInt()]) {
                '.' -> current.getNeighbours(validGrid = grid).filter { p -> map[p.x.toInt()][p.y.toInt()] != '#' }
                '>' -> setOf(current + dRight)
                '<' -> setOf(current + dLeft)
                '^' -> setOf(current + dTop)
                'v' -> setOf(current + dBottom)
                else -> throw IllegalArgumentException("Invalid point on map $current")
            }.filter { it !in visited }
            return if (next.isEmpty()) {
                return 0
            } else if (target in next) {
                stepCount + 1
            } else {
                next.maxOf {
                    getLongestDistance(target, it, stepCount + 1, visited + current)
                }
            }
        }
    }

    fun part1(input: List<String>) = HikingMap(input).longestDistance

    fun part2(input: List<String>) = HikingMap(input).longestDistanceWithoutIce
}

fun main() {
    val testInput = readInput("Day23_test", 2023)
    check(Day23.part1(testInput) == 94)
    check(Day23.part2(testInput) == 154)

    val input = readInput("Day23", 2023)
    println(Day23.part1(input))
    println(Day23.part2(input))
}
