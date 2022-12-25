package aoc2022

import Point
import readInput
import kotlin.math.abs
import kotlin.math.min

object Day24 {

    private enum class Direction(val delta: Point) {
        NORTH(Point(0, -1)), SOUTH(Point(0, 1)), EAST(Point(1, 0)), WEST(Point(-1, 0));

        override fun toString() = when (this) {
            NORTH -> "^"
            SOUTH -> "v"
            EAST -> ">"
            WEST -> "<"
        }

        companion object {
            fun fromChar(char: Char) = when (char) {
                '>' -> EAST
                '<' -> WEST
                '^' -> NORTH
                'v' -> SOUTH
                else -> throw IllegalArgumentException("Not a valid direction: $char")
            }
        }
    }

    private fun Point.move(direction: Direction) = this + direction.delta

    private data class Blizzard(val position: Point, val direction: Direction)

    private data class BlizzardConfig(val blizzards: Collection<Blizzard>) {
        fun next(maxX: Int, maxY: Int) = BlizzardConfig(blizzards.map {
            var newPosition = it.position.move(it.direction)
            if (newPosition.x == 0) {
                newPosition = Point(maxX - 1, newPosition.y)
            } else if (newPosition.x == maxX) {
                newPosition = Point(1, newPosition.y)
            } else if (newPosition.y == 0) {
                newPosition = Point(newPosition.x, maxY - 1)
            } else if (newPosition.y == maxY) {
                newPosition = Point(newPosition.x, 1)
            }
            Blizzard(newPosition, it.direction)
        })

        fun print() {
            for (y in 0..5) {
                for (x in 0..7) {
                    if (x == 0 || y == 0 || x == 7 || y == 5) {
                        print("#")
                    } else {
                        val count = blizzards.count { it.position == Point(x, y) }
                        if (count == 1) {
                            print(blizzards.first { it.position == Point(x, y) }.direction)
                        } else if (count > 1) {
                            print(count.toString())
                        } else {
                            print(".")
                        }
                    }
                }
                println()
            }
        }
    }

    private data class State(val minute: Int, val position: Point, val blizzardConfig: BlizzardConfig)


    fun part1(input: List<String>): Int {
        val map = input.map { row -> row.toCharArray() }.toTypedArray()
        val blizzardConfig = BlizzardConfig(map.withIndex().flatMap { (y, row) ->
            row.withIndex().filter { it.value != '#' && it.value != '.' }.map { (x, direction) ->
                Blizzard(Point(x, y), Direction.fromChar(direction))
            }
        })
        val start = Point(map.first().indexOfFirst { it == '.' }, 0)
        val end = Point(map.last().indexOfFirst { it == '.' }, map.size - 1)
        val validGrid = Point(1, 1) to Point(map.first().size - 1, map.size - 1)

        val minDistances = mutableMapOf<Pair<Point, BlizzardConfig>, Int>()
        val toVisit = mutableListOf(State(0, start, blizzardConfig))
        minDistances[start to blizzardConfig] = 0
        var minDistanceToEnd = Int.MAX_VALUE
        while (toVisit.isNotEmpty()) {
            val current = toVisit.minBy { it.minute + it.position.distanceTo(end) }
            toVisit.remove(current)
            val (currentMinute, currentPosition, currentBlizzardConfig) = current
            if (currentMinute + currentPosition.distanceTo(end) >= minDistanceToEnd) {
                continue
            } else if (currentPosition == end) {
                minDistanceToEnd = min(minDistanceToEnd, currentMinute)
            }
            val neighbours = currentPosition.getNeighbours(validGrid = validGrid).filter { map[it.y][it.x] != '#' }
            val nextConfig = currentBlizzardConfig.next(validGrid.second.x, validGrid.second.y)
            val nextPositions = sequence {
                yield(currentPosition) // also consider waiting at the current position
                yieldAll(neighbours)
            }
            val next = nextPositions.filter { n -> nextConfig.blizzards.find { it.position == n } == null }
                .map { it to nextConfig }
                .filter { !minDistances.contains(it) || minDistances[it]!! > currentMinute + 1 }.onEach {
                    minDistances[it] = currentMinute + 1
                }.map { State(currentMinute + 1, it.first, it.second) }
            toVisit.addAll(next)
        }
        return minDistanceToEnd
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day24_test", 2022)
    check(Day24.part1(testInput) == 18)
    check(Day24.part2(testInput) == 0)

    val input = readInput("Day24", 2022)
    println(Day24.part1(input))
    println(Day24.part2(input))
}
