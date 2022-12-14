package aoc2022

import Point
import readInput

private const val MAX_WIDTH = 1000
private val START = Point(500, 0)
private fun getMaxY(input: List<String>): Int {
    return input.maxOf { path ->
        path.split(" -> ").maxOf { point ->
            val (x, y) = point.split(",").map { it.toInt() }
            y
        }
    }
}

private fun getEmptyGrid(height: Int, width: Int = MAX_WIDTH): Array<CharArray> {
    return Array(height + 1) { CharArray(width + 1) { '.' } }
}

private fun fillGrid(input: List<String>, grid: Array<CharArray>) {
    input.forEach { path ->
        val points = path.split(" -> ").map { point ->
            val (x, y) = point.split(",").map { it.toInt() }
            Point(x, y)
        }
        points.windowed(2, 1).forEach {
            val start = it[0]
            val end = it[1]
            if (start.x == end.x) {
                for (y in minOf(start.y, end.y)..maxOf(start.y, end.y)) {
                    grid[y][start.x] = '#'
                }
            } else {
                for (x in minOf(start.x, end.x)..maxOf(start.x, end.x)) {
                    grid[start.y][x] = '#'
                }
            }
        }
    }
}

private fun addSand(grid: Array<CharArray>, start: Point = START): Int {
    var rounds = 0
    while (true) {
        var currentY = start.y
        var currentX = start.x
        while (true) {
            if (currentY + 1 >= grid.size) {
                // falling of the edge of the grid
                currentY++
                break
            }
            if (grid[currentY + 1][currentX] == '.') {
                // move downwards
                currentY++
            } else if (grid[currentY + 1][currentX - 1] == '.') {
                // move diagonal left
                currentX--
                currentY++
            } else if (grid[currentY + 1][currentX + 1] == '.') {
                // move diagonal right
                currentY++
                currentX++
            } else {
                grid[currentY][currentX] = '+'
                break
            }
        }
        if (currentY >= grid.size) {
            // falling off the edge
            return rounds
        } else if (currentY == start.y && currentX == start.x) {
            // cave filled
            return rounds + 1
        }
        rounds++
    }
}

private fun part1(input: List<String>): Int {
    val grid = getEmptyGrid(height = getMaxY(input))
    fillGrid(input, grid)
    return addSand(grid)
}

private fun part2(input: List<String>): Int {
    val maxY = getMaxY(input) + 2
    val grid = getEmptyGrid(height = maxY)
    val modifiedInput = buildList {
        addAll(input)
        add("0,$maxY -> $MAX_WIDTH,$maxY")
    }
    fillGrid(modifiedInput, grid)
    return addSand(grid)
}

fun main() {
    val testInput = readInput("Day14_test", 2022)
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14", 2022)
    println(part1(input))
    println(part2(input))
}
