package aoc2022

import Point
import readInput

object Day23 {

    private enum class Direction {
        NORTH, SOUTH, WEST, EAST;

        fun getNext() = values()[(this.ordinal + 1) % values().size]
    }

    private fun Point.moveTo(direction: Direction) = when (direction) {
        Direction.NORTH -> this.move(0, -1)
        Direction.SOUTH -> this.move(0, 1)
        Direction.EAST -> this.move(1, 0)
        Direction.WEST -> this.move(-1, 0)
    }

    private fun getAreaOfEnclosingRectangle(map: Collection<Point>): Int {
        val width = map.maxOf { it.x } - map.minOf { it.x } + 1
        val height = map.maxOf { it.y } - map.minOf { it.y } + 1
        return height * width
    }

    @Suppress("unused")
    private fun printMap(map: Collection<Point>) {
        for (y in map.minOf { it.y }..map.maxOf { it.y }) {
            for (x in map.minOf { it.x }..map.maxOf { it.x }) {
                print(if (map.contains(Point(x, y))) '#' else '.')
            }
            println()
        }
    }

    fun part1(input: List<String>): Int {
        var map = input.withIndex().flatMap { (y, line) ->
            line.withIndex().filter { it.value == '#' }.map { it.index }.map { x -> Point(x, y) }
        }

        val findNextPoint: (Point, Direction, Collection<Point>) -> Point? = { position, direction, neighbours ->
            var newPosition: Point? = null
            var currentDirection = direction
            while (newPosition == null) {
                val canMove = when (currentDirection) {
                    Direction.NORTH -> neighbours.filter { it.y == position.y - 1 }.all { !map.contains(it) }
                    Direction.SOUTH -> neighbours.filter { it.y == position.y + 1 }.all { !map.contains(it) }
                    Direction.EAST -> neighbours.filter { it.x == position.x + 1 }.all { !map.contains(it) }
                    Direction.WEST -> neighbours.filter { it.x == position.x - 1 }.all { !map.contains(it) }
                }
                if (canMove) {
                    newPosition = position.moveTo(currentDirection)
                } else {
                    currentDirection = currentDirection.getNext()
                    if (currentDirection == direction) {
                        break
                    }
                }
            }
            newPosition
        }

        var currentDirection = Direction.NORTH
        repeat(10) {

            // key = origin, value = target
            val proposedPositions = mutableMapOf<Point, Point>()
            val duplicates = mutableSetOf<Point>()
            map.map { it to it.getNeighbours(withDiagonal = true).filter { n -> map.contains(n) } }
                .filter { it.second.isNotEmpty() }
                .forEach { (elf, neighbours) ->
                    val nextPosition = findNextPoint(elf, currentDirection, neighbours)
                    if (nextPosition != null) {
                        if (proposedPositions.containsValue(nextPosition)) {
                            duplicates.add(nextPosition)
                        } else {
                            proposedPositions[elf] = nextPosition
                        }
                    }
                }

            val newPositions = proposedPositions.filterValues { !duplicates.contains(it) }

            map = map.map { if (newPositions.containsKey(it)) newPositions[it]!! else it }

            currentDirection = currentDirection.getNext()
        }
        return getAreaOfEnclosingRectangle(map) - map.size
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day23_test", 2022)
    check(Day23.part1(testInput) == 110)
    check(Day23.part2(testInput) == 0)

    val input = readInput("Day23", 2022)
    println(Day23.part1(input))
    println(Day23.part2(input))
}
