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

    private class MapV2(
        private var robot: PointL,
        private val boxesLeft: MutableSet<PointL>,
        private val boxesRight: MutableSet<PointL>,
        private val walls: Set<PointL>,
        private val grid: Pair<PointL, PointL>,
    ) {

        companion object {
            fun fromString(input: List<String>): MapV2 {
                val map = input.to2dCharArray()
                val robotPosition = map.find('@') ?: throw IllegalArgumentException("no robot found")
                val boxLeftPositions = map.findAll("[".toCharArray())
                val boxRightPositions = map.findAll("]".toCharArray())
                val wallPositions = map.findAll("#".toCharArray())
                return MapV2(
                    robot = robotPosition,
                    boxesLeft = boxLeftPositions.keys.toMutableSet(),
                    boxesRight = boxRightPositions.keys.toMutableSet(),
                    walls = wallPositions.keys,
                    grid = map.grid
                )
            }
        }

        val gps: List<Long>
            get() = boxesLeft.map { 100 * it.y + it.x }

        private tailrec fun canMove(
            points: List<PointL>,
            direction: Direction,
            boxesToMove: Set<PointL>
        ): Pair<Boolean, Set<PointL>> {
            val next = points.map { it + direction.delta }
            return if (next.any { it in walls }) {
                false to emptySet()
            } else {
                val boxLeftHits = next.filter { it in boxesLeft }
                val boxRightHits = next.filter { it in boxesRight }
                if (boxLeftHits.isEmpty() && boxRightHits.isEmpty()) {
                    true to boxesToMove
                } else {
                    val newBoxes = boxLeftHits.flatMap { listOf(it, PointL(it.x + 1, it.y)) } +
                            boxRightHits.flatMap { listOf(it, PointL(it.x - 1, it.y)) }
                    canMove(
                        points = newBoxes.filter { it !in points },
                        direction = direction,
                        boxesToMove + newBoxes,
                    )
                }
            }
        }

        fun move(direction: Direction) {
            val delta = direction.delta
            val nextRobotPosition = robot + delta

            val (canMove, toMove) = canMove(listOf(robot), direction, emptySet())

            if (canMove) {
                robot = nextRobotPosition
                toMove.forEach {
                    boxesLeft.remove(it)
                    boxesRight.remove(it)
                }
                val toMoveByY = toMove.map { it + delta }.groupBy { it.y }
                toMoveByY.values.forEach { points ->
                    val sortedByX = points.sortedBy { it.x }
                    var left = true
                    sortedByX.forEach {
                        if (left)
                            boxesLeft.add(it)
                        else
                            boxesRight.add(it)
                        left = !left
                    }
                }
            }
        }

        fun print() {
            grid.print(
                mapOf('[' to boxesLeft, ']' to boxesRight, '#' to walls, '@' to listOf(robot)),
                default = '.'
            )
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
        val (mapStr, directionsStr) = input.separateBy { it.isEmpty() }
        val map = MapV2.fromString(mapStr.map {
            it.replace("#", "##")
                .replace("O", "[]")
                .replace(".", "..")
                .replace("@", "@.")
        })
        val directions = directionsStr.joinToString(separator = "").map { Direction.fromChar(it) }

        directions.forEach { direction ->
            map.move(direction)
        }

        return map.gps.sum().toInt()
    }
}

fun main() {
    val testInputSmall = readInput("Day15_test_small", 2024)
    check(Day15.part1(testInputSmall) == 2028)

    val testInput = readInput("Day15_test", 2024)
    check(Day15.part1(testInput) == 10092)
    check(Day15.part2(testInput) == 9021)

    val input = readInput("Day15", 2024)
    println(Day15.part1(input))
    println(Day15.part2(input))
}
