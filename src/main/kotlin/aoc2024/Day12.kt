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
            get() = points.map { current ->
                val neighbours = current.getNeighbours(withDiagonal = false).filter { points.contains(it) }.size
                4 - neighbours
            }.sum()

        val sides: Int
            get() {
                return 0
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
        return getAreas(input).sumOf { it.area * it.sides }
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
