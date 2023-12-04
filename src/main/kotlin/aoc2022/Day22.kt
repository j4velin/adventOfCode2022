package aoc2022

import Point
import readInput

object Day22 {

    private enum class Rotation {
        COUNTERCLOCKWISE, CLOCKWISE, NONE;

        companion object {
            fun fromChar(c: Char) = when (c) {
                'R' -> CLOCKWISE
                'L' -> COUNTERCLOCKWISE
                else -> NONE
            }
        }
    }

    private data class Movement(val distance: Int, val rotation: Rotation)

    private class Input(val grid: Array<CharArray>, val movements: List<Movement>) {

        companion object {
            fun fromString(input: List<String>): Input {
                val grid = input.takeWhile { it.isNotEmpty() }.map { it.toCharArray() }.toTypedArray()
                val movements =
                    input.dropWhile { it.isNotEmpty() }.drop(1).take(1).first().split(Regex("(?<=[RL])")).map {
                        if (it.endsWith("R") || it.endsWith("L")) {
                            Movement(
                                it.substring(0, it.length - 1).toInt(),
                                Rotation.fromChar(it.takeLast(1).toCharArray().first())
                            )
                        } else {
                            // no rotation on last movement
                            Movement(it.toInt(), Rotation.NONE)
                        }
                    }
                return Input(grid, movements)
            }
        }
    }

    private enum class Direction(val value: Int) {
        UP(3), RIGHT(0), DOWN(1), LEFT(2);

        fun rotate(rotation: Rotation) = when (rotation) {
            Rotation.CLOCKWISE -> values()[((this.ordinal + 1) % values().size)]
            Rotation.COUNTERCLOCKWISE -> values()[((this.ordinal - 1 + values().size) % values().size)]
            Rotation.NONE -> this
        }
    }

    private data class Position(val coords: Point, val direction: Direction) {

        fun move(movement: Movement, grid: Array<CharArray>): Position {
            val diff = when (direction) {
                Direction.UP -> Point(0, -1)
                Direction.DOWN -> Point(0, 1)
                Direction.LEFT -> Point(-1, 0)
                Direction.RIGHT -> Point(1, 0)
            }
            var newCoordinates = coords
            repeat(movement.distance) {
                var newTile = ' '
                var newPoint = newCoordinates
                while (newTile == ' ') {
                    if (direction == Direction.UP || direction == Direction.DOWN) {
                        newPoint = Point(coords.x, (newPoint.y + diff.y + grid.size) % grid.size)
                        // new line might not be as "width" as the current one (there is no ' '-padding on the right side)
                        if (newPoint.x >= grid[newPoint.y].size) {
                            newTile = ' '
                            continue
                        }
                    } else {
                        newPoint = Point((newPoint.x + diff.x + grid[coords.y].size) % grid[coords.y].size, coords.y)
                    }

                    newTile = grid[newPoint.y][newPoint.x]
                }
                when (newTile) {
                    '.' -> newCoordinates = newPoint
                    '#' -> return@repeat
                    else -> throw IllegalStateException("Invalid grid: ${newPoint.x}, ${newPoint.y} == ${grid[newPoint.y][newPoint.x]}")
                }
            }
            val newDirection = direction.rotate(movement.rotation)
            return Position(newCoordinates, newDirection)
        }
    }

    fun part1(input: List<String>): Int {
        val data = Input.fromString(input)
        val startY = data.grid.indexOfFirst { it.contains('.') }
        val startX = data.grid[startY].indexOfFirst { it == '.' }
        var currentPosition = Position(Point(startX, startY), Direction.RIGHT)
        data.movements.forEach {
            currentPosition = currentPosition.move(it, data.grid)
        }
        println(currentPosition)
        return 1000 * (currentPosition.coords.y + 1) + 4 * (currentPosition.coords.x + 1) + currentPosition.direction.value
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day22_test", 2022)
    check(Day22.part1(testInput) == 6032)
    check(Day22.part2(testInput) == 5031)

    val input = readInput("Day22", 2022)
    println(Day22.part1(input))
    println(Day22.part2(input))
}
