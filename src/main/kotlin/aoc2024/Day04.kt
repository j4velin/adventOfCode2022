package aoc2024

import readInput
import to2dCharArray

object Day04 {
    fun part1(input: List<String>): Int {
        val array = input.to2dCharArray()
        val width = array.first().size
        val height = array.size
        var found = 0
        val directions = listOf(
            Pair<Int, Int>(1, 0), // to the right
            Pair<Int, Int>(-1, 0), // to the left
            Pair<Int, Int>(0, 1), // downwards
            Pair<Int, Int>(0, -1), // upwards
            Pair<Int, Int>(1, 1), // diagonal, down,right
            Pair<Int, Int>(1, -1), // diagonal, up,right
            Pair<Int, Int>(-1, 1), // diagonal, down,left
            Pair<Int, Int>(-1, -1), // diagonal, up,left
        )
        for (x in 0 until width) {
            for (y in 0 until height) {
                // starting character must be an 'X'
                if (array[x][y] == 'X') {
                    found += directions.filter { (dx, dy) ->
                        val maxX = x + 3 * dx
                        val maxY = y + 3 * dy
                        val fitsInGrid = maxX in 0 until width && maxY in 0 until height
                        fitsInGrid && (0..3).map { array[x + it * dx][y + it * dy] }.joinToString("") == "XMAS"
                    }.count()
                }
            }
        }
        return found
    }

    fun part2(input: List<String>): Int {
        val array = input.to2dCharArray()
        val width = array.first().size
        val height = array.size
        var found = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                // center must be an 'A' and there must be enough space for the other characters in the array
                if (array[x][y] == 'A' && x >= 1 && y >= 1 && x + 1 < width && y + 1 < height) {
                    val topLeftToBottomRightMatches = (array[x - 1][y - 1] == 'M' && array[x + 1][y + 1] == 'S') ||
                            (array[x - 1][y - 1] == 'S' && array[x + 1][y + 1] == 'M')
                    val topRightToBottomLeftMatches = (array[x + 1][y - 1] == 'M' && array[x - 1][y + 1] == 'S') ||
                            (array[x + 1][y - 1] == 'S' && array[x - 1][y + 1] == 'M')
                    if (topLeftToBottomRightMatches && topRightToBottomLeftMatches) found++
                }
            }
        }
        return found
    }
}

fun main() {
    val testInput = readInput("Day04_test", 2024)
    check(Day04.part1(testInput) == 18)
    check(Day04.part2(testInput) == 9)

    val input = readInput("Day04", 2024)
    println(Day04.part1(input))
    println(Day04.part2(input))
}
