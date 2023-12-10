package aoc2023

import Point
import readInput
import to2dCharArray

private data class PipeNetwork(val map: Array<CharArray>, val start: Point) {
    companion object {
        fun fromStrings(input: List<String>): PipeNetwork {
            val map = input.to2dCharArray()
            val start = map.withIndex().firstNotNullOf { (x, column) ->
                column.withIndex().find { (y, char) -> char == 'S' }?.index?.let { y -> Point(x, y) }
            }
            return PipeNetwork(map, start)
        }

        fun fromStringsExtended(input: List<String>): PipeNetwork {
            val map = Array(input.first().length * 2) { CharArray(input.size * 2) { '.' } }
            for (y in input.indices) {
                for ((x, char) in input[y].withIndex()) {
                    map[x * 2][y * 2] = char
                    map[x * 2 + 1][y * 2] = when (char) {
                        '|' -> '.'
                        '-' -> '-'
                        'L' -> '-'
                        'J' -> '.'
                        '7' -> '.'
                        'F' -> '-'
                        '.' -> '.'
                        'S' -> '+'
                        else -> throw IllegalArgumentException("invalid input at $x,$y: $char")
                    }
                    map[x * 2][y * 2 + 1] = when (char) {
                        '|' -> '|'
                        '-' -> '.'
                        'L' -> '.'
                        'J' -> '.'
                        '7' -> '|'
                        'F' -> '|'
                        '.' -> '.'
                        'S' -> '+'
                        else -> throw IllegalArgumentException("invalid input at $x,$y: $char")
                    }
                }
            }

            val start = map.withIndex().firstNotNullOf { (x, column) ->
                column.withIndex().find { (y, char) -> char == 'S' }?.index?.let { y -> Point(x, y) }
            }
            return PipeNetwork(map, start)
        }
    }

    val loop by lazy { findMainLoop() }
    val validGrid = Point(0, 0) to Point(map.size - 1, map.first().size - 1)

    private fun findMainLoop(): Set<Point> {
        val neighbours = start.getNeighbours(validGrid = validGrid)
        return neighbours.firstNotNullOf { it.getMainLoop(listOf(start)) }.toSet()
    }

    private tailrec fun Point.getMainLoop(history: List<Point>): List<Point>? {
        val thisPipe = map[x][y]
        if (thisPipe == '.') return null

        val newHistory = buildList {
            addAll(history)
            add(this@getMainLoop)
        }

        if (thisPipe == 'S') return newHistory

        val nextPipe = getNext().filter { map[it.x][it.y] != '.' }.firstOrNull { it != history.last() }

        return nextPipe?.getMainLoop(newHistory)
    }

    private fun Point.getNext(): List<Point> {
        val thisPipe = map[x][y]
        val north = if (y > 0) Point(x, y - 1) else null
        val south = if (y < map.first().size - 1) Point(x, y + 1) else null
        val east = if (x < map.size - 1) Point(x + 1, y) else null
        val west = if (x > 0) Point(x - 1, y) else null

        val next = when (thisPipe) {
            /*
            | is a vertical pipe connecting north and south.
            - is a horizontal pipe connecting east and west.
            L is a 90-degree bend connecting north and east.
            J is a 90-degree bend connecting north and west.
            7 is a 90-degree bend connecting south and west.
            F is a 90-degree bend connecting south and east.
             */
            '|' -> listOf(north, south)
            '-' -> listOf(east, west)
            'L' -> listOf(north, east)
            'J' -> listOf(north, west)
            '7' -> listOf(south, west)
            'F' -> listOf(south, east)
            '+' -> listOf(north, south, east, west) // additional "+" for the part2 quiz
            else -> throw IllegalArgumentException("invalid input at $this: $thisPipe")
        }

        return next.filterNotNull()
    }

}

private fun growArea(pipeNetwork: PipeNetwork, area: MutableSet<Point>) {
    var sizeBefore = 0
    while (area.size > sizeBefore) {
        sizeBefore = area.size
        val additions = mutableSetOf<Point>()
        area.forEach {
            additions.addAll(it.getNeighbours(validGrid = pipeNetwork.validGrid).filter { n -> n !in pipeNetwork.loop })
        }
        area.addAll(additions)
    }
}

object Day10 {
    fun part1(input: List<String>) = PipeNetwork.fromStrings(input).loop.size / 2

    fun part2(input: List<String>): Int {
        val pipeNetwork = PipeNetwork.fromStringsExtended(input)
        val outsideTiles = mutableSetOf<Point>()
        val enclosedTiles = mutableSetOf<Point>()

        // add all points on the edge, which are not part of the loop, to the "outside" set
        for (x in pipeNetwork.map.indices) {
            var p = Point(x, 0)
            if (p !in pipeNetwork.loop) {
                outsideTiles.add(p)
            }
            p = Point(x, pipeNetwork.map.first().size - 1)
            if (p !in pipeNetwork.loop) {
                outsideTiles.add(p)
            }
        }
        for (y in pipeNetwork.map.first().indices) {
            var p = Point(0, y)
            if (p !in pipeNetwork.loop) {
                outsideTiles.add(p)
            }
            p = Point(pipeNetwork.map.size - 1, y)
            if (p !in pipeNetwork.loop) {
                outsideTiles.add(p)
            }
        }

        // blow up the outside area until all reachable tiles are part of it (BFS)
        growArea(pipeNetwork, outsideTiles)

        // print extended map and add all "unaccounted tiles" to the "enclosedTiles" set
        for (y in pipeNetwork.map.first().indices) {
            for (x in pipeNetwork.map.indices) {
                when (val point = Point(x, y)) {
                    in pipeNetwork.loop -> print(pipeNetwork.map[x][y])
                    in outsideTiles -> print('O')
                    else -> {
                        print(" ")
                        enclosedTiles.add(point)
                    }
                }
            }
            println()
        }

        // shrink to original size by ignoring all added columns & rows
        return enclosedTiles.filter { it.x % 2 == 0 && it.y % 2 == 0 }.size
    }
}

fun main() {
    val testInput = readInput("Day10_test", 2023)
    val testInput2 = """FF7FSF7F7F7F7F7F---7
L|LJ||||||||||||F--J
FL-7LJLJ||||||LJL-77
F--JF--7||LJLJ7F7FJ-
L---JF-JLJ.||-FJLJJ7
|F|F-JF---7F7-L7L|7|
|FFJF7L7F-JF7|JL---7
7-L-JL7||F7|L7F-7F7|
L.L7LFJ|||||FJL7||LJ
L7JLJL-JLJLJL--JLJ.L"""
    check(Day10.part1(testInput) == 8)
    check(Day10.part2(testInput2.split("\n")) == 10)

    val input = readInput("Day10", 2023)
    println(Day10.part1(input))
    println(Day10.part2(input))
}
