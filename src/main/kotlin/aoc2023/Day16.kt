package aoc2023

import Point
import readInput
import to2dCharArray
import java.util.*
import kotlin.math.max

object Day16 {

    private enum class Direction { TOP, BOTTOM, LEFT, RIGHT }
    private data class Movement(val comingFrom: Direction, val position: Point)

    private class ContraptionGrid(input: List<String>) {
        private val tiles = input.to2dCharArray()
        private val maxX = input.first().length - 1
        private val maxY = input.size - 1
        private val grid = Pair(Point(0, 0), Point(maxX, maxY))

        fun getEnergizedTileCount(startDirection: Direction = Direction.LEFT, startPosition: Point = Point(0, 0)): Int {
            val visitedTiles = mutableSetOf<Movement>()
            val toVisit: Queue<Movement> = LinkedList()
            toVisit.add(Movement(startDirection, startPosition))
            while (toVisit.isNotEmpty()) {
                val current = toVisit.poll()
                if (!visitedTiles.contains(current)) {
                    visitedTiles.add(current)
                    val (direction, position) = current
                    val tile = tiles[position.x][position.y]
                    toVisit.addAll(tile.getNext(direction, position).filter { (_, next) -> next.isWithin(grid) })
                }
            }
            return visitedTiles.map { it.position }.toSet().size
        }

        val maxEnergizedTileCount by lazy {
            // seems fast enough so that we don't even need any cache
            var max = 0
            for (x in 0..maxX) {
                max = max(max, getEnergizedTileCount(Direction.BOTTOM, Point(x, maxY)))
                max = max(max, getEnergizedTileCount(Direction.TOP, Point(x, 0)))
            }
            for (y in 0..maxY) {
                max = max(max, getEnergizedTileCount(Direction.RIGHT, Point(maxX, y)))
                max = max(max, getEnergizedTileCount(Direction.LEFT, Point(0, y)))
            }
            max
        }
    }

    /**
     * @param comingFrom the direction, from which we enter this tile
     * @param position the position of this tile
     * @return a list of adjacent tiles and the direction, in which we enter them
     */
    private fun Char.getNext(comingFrom: Direction, position: Point): List<Movement> {
        val top = Movement(Direction.BOTTOM, position.move(0, -1))
        val bottom = Movement(Direction.TOP, position.move(0, 1))
        val left = Movement(Direction.RIGHT, position.move(-1, 0))
        val right = Movement(Direction.LEFT, position.move(1, 0))
        return when (this) {
            '.' -> when (comingFrom) {
                Direction.TOP -> listOf(bottom)
                Direction.BOTTOM -> listOf(top)
                Direction.LEFT -> listOf(right)
                Direction.RIGHT -> listOf(left)
            }

            '/' -> when (comingFrom) {
                Direction.TOP -> listOf(left)
                Direction.BOTTOM -> listOf(right)
                Direction.LEFT -> listOf(top)
                Direction.RIGHT -> listOf(bottom)
            }

            '\\' -> when (comingFrom) {
                Direction.TOP -> listOf(right)
                Direction.BOTTOM -> listOf(left)
                Direction.LEFT -> listOf(bottom)
                Direction.RIGHT -> listOf(top)
            }

            '-' -> when (comingFrom) {
                Direction.TOP, Direction.BOTTOM -> listOf(left, right)
                Direction.LEFT -> listOf(right)
                Direction.RIGHT -> listOf(left)
            }

            '|' -> when (comingFrom) {
                Direction.TOP -> listOf(bottom)
                Direction.BOTTOM -> listOf(top)
                Direction.LEFT, Direction.RIGHT -> listOf(top, bottom)
            }

            else -> throw IllegalArgumentException("Unknown input: $this for tile at $position")
        }
    }


    fun part1(input: List<String>) = ContraptionGrid(input).getEnergizedTileCount()

    fun part2(input: List<String>) = ContraptionGrid(input).maxEnergizedTileCount
}

fun main() {
    val testInput = readInput("Day16_test", 2023)
    check(Day16.part1(testInput) == 46)
    check(Day16.part2(testInput) == 51)

    val input = readInput("Day16", 2023)
    println(Day16.part1(input))
    println(Day16.part2(input))
}
