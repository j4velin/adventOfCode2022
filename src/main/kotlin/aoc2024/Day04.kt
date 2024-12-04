package aoc2024

import readInput
import to2dCharArray

object Day04 {
    fun part1(input: List<String>): Int {
        val array = input.to2dCharArray()
        val width = array.first().size
        val height = array.size
        var found = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                // forward
                if (x + 3 < width && "${array[x][y]}${array[x + 1][y]}${array[x + 2][y]}${array[x + 3][y]}" == "XMAS") found++
                // backward
                if (x >= 3 && "${array[x][y]}${array[x - 1][y]}${array[x - 2][y]}${array[x - 3][y]}" == "XMAS") found++
                // down
                if (y + 3 < height && "${array[x][y]}${array[x][y + 1]}${array[x][y + 2]}${array[x][y + 3]}" == "XMAS") found++
                // up
                if (y >= 3 && "${array[x][y]}${array[x][y - 1]}${array[x][y - 2]}${array[x][y - 3]}" == "XMAS") found++
                // diagonal down,right
                if (x + 3 < width && y + 3 < height && "${array[x][y]}${array[x + 1][y + 1]}${array[x + 2][y + 2]}${array[x + 3][y + 3]}" == "XMAS") found++
                // diagonal, down,left
                if (x >= 3 && y + 3 < height && "${array[x][y]}${array[x - 1][y + 1]}${array[x - 2][y + 2]}${array[x - 3][y + 3]}" == "XMAS") found++
                // diagonal up,right
                if (x + 3 < width && y >= 3 && "${array[x][y]}${array[x + 1][y - 1]}${array[x + 2][y - 2]}${array[x + 3][y - 3]}" == "XMAS") found++
                // diagonal, up,left
                if (x >= 3 && y >= 3 && "${array[x][y]}${array[x - 1][y - 1]}${array[x - 2][y - 2]}${array[x - 3][y - 3]}" == "XMAS") found++
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
                if (array[x][y] == 'A' && x >= 1 && y >= 1 && x + 1 < width && y + 1 < height) {
                    val topLeftToBottomRight = (array[x - 1][y - 1] == 'M' && array[x + 1][y + 1] == 'S') ||
                            (array[x - 1][y - 1] == 'S' && array[x + 1][y + 1] == 'M')
                    val topRightToBottomLeft = (array[x + 1][y - 1] == 'M' && array[x - 1][y + 1] == 'S') ||
                            (array[x + 1][y - 1] == 'S' && array[x - 1][y + 1] == 'M')
                    if (topLeftToBottomRight && topRightToBottomLeft) found++
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
