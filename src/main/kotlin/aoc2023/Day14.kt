package aoc2023

import print
import readInput
import to2dCharArray

private enum class Direction { NORTH, SOUTH, EAST, WEST }
private class MirrorMap(input: List<String>) {

    private var currentMap = input.to2dCharArray()
    private val maxY = input.size
    private val maxX = currentMap.size

    val northLoad: Int
        get() {
            var northLoad = 0
            currentMap.withIndex().forEach { (x, column) ->
                column.withIndex().forEach { (y, char) ->
                    if (char == 'O') northLoad += maxY - y
                }
            }
            return northLoad
        }

    fun cycle(times: Int) {
        // north, then west, then south, then east
        /*
        repeat(times) {
            tilt(Direction.NORTH)
            tilt(Direction.WEST)
            tilt(Direction.SOUTH)
            tilt(Direction.EAST)
        }

         */
    }

    fun tilt(direction: Direction) {
        val tiltedMap = Array(currentMap.size) { CharArray(currentMap.first().size) { '.' } }
        currentMap.withIndex().forEach { (x, column) ->
            column.withIndex().forEach { (y, char) ->
                if (char == '#') tiltedMap[x][y] = char
            }
        }

        val deltaY = when (direction) {
            Direction.NORTH -> -1
            Direction.SOUTH -> 1
            else -> 0
        }
        val deltaX = when (direction) {
            Direction.EAST -> 1
            Direction.WEST -> -1
            else -> 0
        }
        currentMap.withIndex().forEach { (x, column) ->
            column.withIndex().forEach { (y, char) ->
                if (char == 'O') {
                    var currentY = y
                    var currentX = x
                    while (currentY + deltaY in 0..<maxY && currentX + deltaX in 0..<maxX
                        && tiltedMap[currentX + deltaX][currentY + deltaY] == '.'
                    ) {
                        currentY += deltaY
                        currentX += deltaX
                    }
                    // position already taken -> move backward again
                    while (tiltedMap[currentX][currentY] == char) {
                        currentY -= deltaY
                        currentX -= deltaX
                    }
                    tiltedMap[currentX][currentY] = char
                }
            }
        }
        currentMap = tiltedMap
    }

}

object Day14 {

    fun part1(input: List<String>): Int {
        val map = MirrorMap(input)
        map.tilt(Direction.NORTH)
        return map.northLoad
    }

    fun part2(input: List<String>): Int {
        val map = MirrorMap(input)
        map.cycle(1_000_000_000)
        return map.northLoad
    }
}

fun main() {
    val testInput = readInput("Day14_test", 2023)
    check(Day14.part1(testInput) == 136)
    check(Day14.part2(testInput) == 64)

    val input = readInput("Day14", 2023)
    println(Day14.part1(input))
    println(Day14.part2(input))
}
