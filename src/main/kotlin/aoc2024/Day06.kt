package aoc2024

import PointL
import readInput
import to2dCharArray

object Day06 {

    private open class Map(private val input: Array<CharArray>) {
        private val maxX = input.first().size - 1
        private val maxY = input.size - 1
        val grid = PointL(0, 0) to PointL(maxX, maxY)

        fun get(x: Long, y: Long) = get(x.toInt(), y.toInt())

        open fun get(x: Int, y: Int) = input[x][y]

        fun findGuard(): Guard {
            for (x in 0..<maxX) {
                for (y in 0..<maxY) {
                    if (get(x, y) == '^') {
                        return Guard(PointL(x, y), Direction.NORTH)
                    }
                }
            }
            throw IllegalArgumentException("No guard found")
        }
    }

    private class MapWithObstacle(input: Array<CharArray>, private val additionalObstacle: PointL) : Map(input) {
        override fun get(x: Int, y: Int) =
            if (additionalObstacle.x.toInt() == x && additionalObstacle.y.toInt() == y) '#' else super.get(x, y)

        val hasLoop: Boolean by lazy {
            var loopFound = false
            val guard = findGuard()
            val visited = mutableSetOf<Pair<PointL, Direction>>()
            while (guard.position.isWithin(this)) {
                if (!visited.add(guard.position to guard.direction)) {
                    loopFound = true
                    break
                }
                guard.move(this)
            }
            loopFound
        }
    }

    private enum class Direction(val delta: PointL) {
        NORTH(PointL(0, -1)),
        EAST(PointL(1, 0)),
        SOUTH(PointL(0, 1)),
        WEST(PointL(-1, 0)),
    }

    private fun PointL.isWithin(map: Map) = this.isWithin(map.grid)

    private class Guard(var position: PointL, var direction: Direction) {

        fun move(map: Map) {
            var possibleDirection = direction
            var possiblePosition = position + possibleDirection.delta
            while (possiblePosition.isWithin(map) && map.get(possiblePosition.x, possiblePosition.y) == '#') {
                possibleDirection = when (possibleDirection) {
                    Direction.NORTH -> Direction.EAST
                    Direction.EAST -> Direction.SOUTH
                    Direction.SOUTH -> Direction.WEST
                    Direction.WEST -> Direction.NORTH
                }
                if (possibleDirection == direction) {
                    throw IllegalArgumentException("Impossible to move, blocked in all directions")
                }
                possiblePosition = position + possibleDirection.delta
            }

            position = possiblePosition
            direction = possibleDirection
        }
    }

    fun part1(input: List<String>): Int {
        val map = Map(input.to2dCharArray())
        val guard = map.findGuard()
        val visited = mutableSetOf<PointL>()
        while (guard.position.isWithin(map)) {
            visited.add(guard.position)
            guard.move(map)
        }
        return visited.size
    }

    fun part2(input: List<String>): Int {
        val inputArray = input.to2dCharArray()
        val map = Map(inputArray)
        val guard = map.findGuard()
        val start = guard.position
        val visited = mutableSetOf<PointL>()
        while (guard.position.isWithin(map)) {
            visited.add(guard.position)
            guard.move(map)
        }

        val possiblePlacesForNewObstacle = visited - start
        val loops = possiblePlacesForNewObstacle.count { newObstaclePosition ->
            MapWithObstacle(inputArray, newObstaclePosition).hasLoop
        }
        return loops
    }
}

fun main() {
    val testInput = readInput("Day06_test", 2024)
    check(Day06.part1(testInput) == 41)
    check(Day06.part2(testInput) == 6)

    val input = readInput("Day06", 2024)
    println(Day06.part1(input))
    println(Day06.part2(input))
}
