package aoc2024

import Direction
import PointL
import find
import findAll
import grid
import print
import readInput
import separateBy
import to2dCharArray

object Day15 {

    private class Map(
        private var robot: PointL,
        private val boxes: MutableSet<PointL>,
        private val walls: Set<PointL>,
        private val grid: Pair<PointL, PointL>,
    ) {

        companion object {
            fun fromString(input: List<String>): Map {
                val map = input.to2dCharArray()
                val robotPosition = map.find('@') ?: throw IllegalArgumentException("no robot found")
                val boxPositions = map.findAll("O".toCharArray())
                val wallPositions = map.findAll("#".toCharArray())
                return Map(
                    robot = robotPosition,
                    boxes = boxPositions.keys.toMutableSet(),
                    walls = wallPositions.keys,
                    grid = map.grid
                )
            }
        }

        val gps: List<Long>
            get() = boxes.map { 100 * it.y + it.x }

        fun move(direction: Direction) {
            val delta = direction.delta
            val nextRobotPosition = robot + delta
            if (nextRobotPosition !in boxes && nextRobotPosition !in walls) {
                // move to empty space
                robot = nextRobotPosition
            } else {
                val toMove =
                    generateSequence(1) { it + 1 }.map { robot + delta * it }.takeWhile { it in boxes }.toList()
                if (toMove.isNotEmpty() && (toMove.last() + delta) !in walls) {
                    // can move -> first remove all boxes which needs to be moved, then add them again at their new position
                    // (we can't do both operations at once due to using a 'Set' for the boxes and not a 'List'
                    toMove.forEach {
                        boxes.remove(it)
                    }
                    toMove.forEach {
                        boxes.add(it + delta)
                    }
                    robot += delta
                }
            }
        }

        fun print() {
            grid.print(mapOf('0' to boxes, '#' to walls, '@' to listOf(robot)), default = '.')
        }
    }

    fun part1(input: List<String>): Int {
        val (mapStr, directionsStr) = input.separateBy { it.isEmpty() }
        val map = Map.fromString(mapStr)
        val directions = directionsStr.joinToString(separator = "").map { Direction.fromChar(it) }
        directions.forEach { direction ->
            map.move(direction)
        }

        return map.gps.sum().toInt()
    }

    fun part2(input: List<String>): Int {
         return 0
    }
}

fun main() {
    val testInputSmall = readInput("Day15_test_small", 2024)
    check(Day15.part1(testInputSmall) == 2028)

    val testInput = readInput("Day15_test", 2024)
    check(Day15.part1(testInput) == 10092)
    //check(Day15.part2(testInput) == 9021)

    val input = readInput("Day15", 2024)
    println(Day15.part1(input))
    println(Day15.part2(input))
}
