package aoc2022

import Point
import readInput

object Day23 {

    private enum class Direction {
        NORTH, SOUTH, WEST, EAST;

        fun getNext() = values()[(this.ordinal + 1) % values().size]
    }

    /**
     * Moves this point into the given [Direction]
     */
    private fun Point.moveTo(direction: Direction) = when (direction) {
        Direction.NORTH -> this.move(0, -1)
        Direction.SOUTH -> this.move(0, 1)
        Direction.EAST -> this.move(1, 0)
        Direction.WEST -> this.move(-1, 0)
    }

    /**
     * Gets the minimum area which covers all the given [Point]s
     */
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

    /**
     * Finds the new position, to which [position] can move to
     *
     * @param position the origin position
     * @param direction the first direction to look for new positions
     * @param neighbours all the neighbouring position for [position]
     * @param map the map of all the elves' positions before any
     * @return the new position or null, if none is found
     */
    private fun findNextPoint(
        position: Point,
        direction: Direction,
        neighbours: Collection<Point>,
        map: Collection<Point>
    ): Point? {
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
        return newPosition
    }

    /**
     * Gets a map of (current elf position) to (new elf position) for all the elves, that can move in this round
     * @param map the map of all the elves before any movement
     * @param currentDirection the current "start" direction
     */
    private fun getNewElfPositions(map: Collection<Point>, currentDirection: Direction): Map<Point, Point> {
        // key = origin, value = target
        val proposedPositions = mutableMapOf<Point, Point>()
        val duplicates = mutableSetOf<Point>() // points to which multiple elves want to move
        map.map { it to it.getNeighbours(withDiagonal = true).filter { n -> map.contains(n) } }
            .filter { it.second.isNotEmpty() }
            .forEach { (elf, neighbours) ->
                val nextPosition = findNextPoint(elf, currentDirection, neighbours, map)
                if (nextPosition != null) {
                    if (proposedPositions.containsValue(nextPosition)) {
                        duplicates.add(nextPosition)
                    } else {
                        proposedPositions[elf] = nextPosition
                    }
                }
            }

        return proposedPositions.filterValues { !duplicates.contains(it) }
    }

    fun part1(input: List<String>): Int {
        var map = input.withIndex().flatMap { (y, line) ->
            line.withIndex().filter { it.value == '#' }.map { it.index }.map { x -> Point(x, y) }
        }

        var currentDirection = Direction.NORTH
        repeat(10) {
            val newPositions = getNewElfPositions(map, currentDirection)
            map = map.map { if (newPositions.containsKey(it)) newPositions[it]!! else it }
            currentDirection = currentDirection.getNext()
        }
        return getAreaOfEnclosingRectangle(map) - map.size
    }

    fun part2(input: List<String>): Int {
        var map = input.withIndex().flatMap { (y, line) ->
            line.withIndex().filter { it.value == '#' }.map { it.index }.map { x -> Point(x, y) }
        }

        var currentDirection = Direction.NORTH
        var round = 0
        var elvesMoved = -1
        while (elvesMoved != 0) {
            round++
            val newPositions = getNewElfPositions(map, currentDirection)
            elvesMoved = newPositions.size
            map = map.map { if (newPositions.containsKey(it)) newPositions[it]!! else it }
            currentDirection = currentDirection.getNext()
        }
        return round
    }
}

fun main() {
    val testInput = readInput("Day23_test", 2022)
    check(Day23.part1(testInput) == 110)
    check(Day23.part2(testInput) == 20)

    val input = readInput("Day23", 2022)
    println(Day23.part1(input))
    println(Day23.part2(input))
}
