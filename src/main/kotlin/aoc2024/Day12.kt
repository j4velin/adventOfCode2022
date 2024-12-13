package aoc2024

import PointL
import get
import grid
import readInput
import to2dCharArray
import java.util.LinkedList
import java.util.Queue

object Day12 {

    private data class Area(val char: Char, val points: MutableSet<PointL> = mutableSetOf()) {

        val area: Int
            get() = points.size

        val perimeter: Int
            get() = points.sumOf { current ->
                val neighbours = current.getNeighbours(withDiagonal = false).filter { points.contains(it) }.size
                4 - neighbours
            }

        val corners: Int
            get() {
                val minX = points.minOf { it.x }
                val maxX = points.maxOf { it.x }
                val minY = points.minOf { it.y }
                val maxY = points.maxOf { it.y }
                var corners = 0
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        corners += countCorners(PointL(x, y))
                    }
                }
                return corners
            }

        private fun countCorners(point: PointL): Int {
            val neighbours = point.getNeighbours(withDiagonal = true).filter { points.contains(it) }
            val hasLeftNeighbour = neighbours.contains(PointL(point.x - 1, point.y))
            val hasRightNeighbour = neighbours.contains(PointL(point.x + 1, point.y))
            val hasTopNeighbour = neighbours.contains(PointL(point.x, point.y - 1))
            val hasBottomNeighbour = neighbours.contains(PointL(point.x, point.y + 1))

            val hasTopRightNeighbour = neighbours.contains(PointL(point.x + 1, point.y - 1))
            val hasTopLeftNeighbour = neighbours.contains(PointL(point.x - 1, point.y - 1))
            val hasBottomRightNeighbour = neighbours.contains(PointL(point.x + 1, point.y + 1))
            val hasBottomLeftNeighbour = neighbours.contains(PointL(point.x - 1, point.y + 1))

            var corners = 0

            if (point in points) {
                // outside pointing corner
                if (!hasTopNeighbour) {
                    if (!hasLeftNeighbour) {
                        corners++
                    }
                    if (!hasRightNeighbour) {
                        corners++
                    }
                }

                if (!hasBottomNeighbour) {
                    if (!hasLeftNeighbour) {
                        corners++
                    }
                    if (!hasRightNeighbour) {
                        corners++
                    }
                }

                // inside pointing corner
                if (!hasTopRightNeighbour && hasTopNeighbour && hasRightNeighbour) {
                    corners++
                }
                if (!hasTopLeftNeighbour && hasTopNeighbour && hasLeftNeighbour) {
                    corners++
                }
                if (!hasBottomRightNeighbour && hasBottomNeighbour && hasRightNeighbour) {
                    corners++
                }
                if (!hasBottomLeftNeighbour && hasBottomNeighbour && hasLeftNeighbour) {
                    corners++
                }
            }

            return corners
        }
    }


    private fun getAreas(input: List<String>): Set<Area> {
        val map = input.to2dCharArray()
        val visitedPoints = mutableSetOf<PointL>()
        val areas = mutableSetOf<Area>()
        val grid = map.grid
        for (x in map.indices) {
            for (y in map[0].indices) {
                val current = PointL(x, y)
                if (!visitedPoints.contains(current)) {
                    val currentType = map[x][y]
                    val newArea = Area(currentType)
                    areas.add(newArea)
                    val toVisit: Queue<PointL> = LinkedList(listOf(current))

                    while (toVisit.isNotEmpty()) {
                        val next = toVisit.remove()
                        newArea.points.add(next)
                        visitedPoints.add(next)
                        val toAdd = next.getNeighbours(withDiagonal = false, validGrid = grid)
                            .filter { map.get(it.x, it.y) == currentType }
                            .filter { !visitedPoints.contains(it) }
                            .filter { !toVisit.contains(it) }
                        toVisit.addAll(toAdd)
                    }
                }
            }
        }
        return areas
    }

    fun part1(input: List<String>): Int {
        return getAreas(input).sumOf { it.area * it.perimeter }
    }

    fun part2(input: List<String>): Int {

        check(
            getAreas(
                """..............
....xx........
xxxxxxxxxx....
xxxx...xxx....
xxxxx..xxxxxxx
xxxx...xxx....
xxxxxxxxxx....
xxx....xxx....""".lines()
            ).find { it.char == 'x' }!!.corners == 24
        )


        return getAreas(input)
            //.onEach { println("${it.char} -> ${it.area} x ${it.corners}") }
            .sumOf { it.area * it.corners }
    }
}

fun main() {
    val testInput = readInput("Day12_test", 2024)
    check(Day12.part1(testInput) == 1930)
    check(Day12.part2(testInput) == 1206)

    val input = readInput("Day12", 2024)
    println(Day12.part1(input))
    println(Day12.part2(input))
}
