package aoc2023

import Point
import multiplyOf
import readInput

private data class SchemanticsChar(val point: Point, val char: Char)

object Day03 {

    /**
     * Reads the complete part number starting from any position within the number
     *
     * @param allPoints all possible [SchemanticsChar] in the original input
     * @param part some character within the part number to find
     * @return a pair of the complete part number, in which [part] is one digit and its starting position (to avoid duplicates)
     */
    private fun getPartNumber(allPoints: List<SchemanticsChar>, part: SchemanticsChar): Pair<Point, Int> {
        var partNumber = part.char.toString()
        var startPosition = part.point

        // move right
        var nextPoint = part.point
        while (true) {
            nextPoint = nextPoint.move(1, 0)
            val nextPart = allPoints.find { it.point == nextPoint }
            if (nextPart != null && nextPart.char.isDigit()) {
                partNumber += nextPart.char
            } else {
                break
            }
        }

        // move left
        nextPoint = part.point
        while (true) {
            nextPoint = nextPoint.move(-1, 0)
            val nextPart = allPoints.find { it.point == nextPoint }
            if (nextPart != null && nextPart.char.isDigit()) {
                partNumber = nextPart.char + partNumber
                startPosition = nextPoint
            } else {
                break
            }
        }
        return Pair(startPosition, partNumber.toInt())
    }

    private fun parseInput(input: List<String>) = input.withIndex()
        .flatMap { indexLine ->
            val y = indexLine.index
            val line = indexLine.value
            line.withIndex().map { indexChar ->
                SchemanticsChar(Point(indexChar.index, y), indexChar.value)
            }
        }

    fun part1(input: List<String>): Int {
        val allPoints = parseInput(input)
        val validGrid = Pair(Point(0, 0), Point(input[0].length, input.size))
        return allPoints.filter { it.char != '.' && !it.char.isDigit() }
            .flatMap { symbol ->
                symbol.point.getNeighbours(withDiagonal = true, validGrid)
                    .mapNotNull { neighbour -> allPoints.find { it.point == neighbour } }
                    .filter { it.char.isDigit() }
                    .map { getPartNumber(allPoints, it) }
                    .toSet() // eliminate duplicates
            }.sumOf { it.second }
    }

    fun part2(input: List<String>): Int {
        val allPoints = parseInput(input)
        val validGrid = Pair(Point(0, 0), Point(input[0].length, input.size))
        return allPoints.filter { it.char == '*' }
            .sumOf { gear ->
                val neighbourParts = gear.point.getNeighbours(withDiagonal = true, validGrid)
                    .mapNotNull { neighbour -> allPoints.find { it.point == neighbour } }
                    .filter { it.char.isDigit() }
                    .map { getPartNumber(allPoints, it) }
                    .toSet()
                if (neighbourParts.size >= 2) {
                    neighbourParts.multiplyOf { it.second }
                } else {
                    0
                }
            }
    }
}

fun main() {
    val testInput = readInput("Day03_test", 2023)
    check(Day03.part1(testInput) == 4361)
    check(Day03.part2(testInput) == 467835)

    val input = readInput("Day03", 2023)
    println(Day03.part1(input))
    println(Day03.part2(input))
}
