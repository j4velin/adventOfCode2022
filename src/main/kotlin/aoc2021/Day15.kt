package aoc2021

import Point
import modulo
import readInput

/**
 * @param inputMap the input risk map, see [getInputMap]
 * @return the lowest possible risk of all paths from the upper left corner to the bottom right corner
 */
private fun getLowestRisk(inputMap: Array<IntArray>): Int {
    val maxX = (inputMap.firstOrNull()?.size ?: 1) - 1
    val maxY = inputMap.size - 1
    val validGrid = Pair(Point(0, 0), Point(maxX, maxY))
    val lowestRisks = Array(maxY + 1) { IntArray(maxX + 1) }
    val risksChanged = mutableSetOf<Point>()
    risksChanged.add(Point(0, 0))
    fun updateRisk(riskToHere: Int, point: Point) {
        val currentRisk = lowestRisks[point.x][point.y]
        val newRisk = riskToHere + inputMap[point.x][point.y]
        val wasVisitedBefore = currentRisk > 0 || (point.x == 0 && point.y == 0)
        if (!wasVisitedBefore || currentRisk > newRisk) {
            risksChanged.add(point)
            lowestRisks[point.x][point.y] = newRisk
        }
    }
    while (risksChanged.isNotEmpty()) {
        val current = risksChanged.first()
        risksChanged.remove(current)
        val riskToHere = lowestRisks[current.x][current.y]
        current.getNeighbours(validGrid = validGrid).forEach { updateRisk(riskToHere, it) }
    }
    return lowestRisks[maxX][maxY]
}

/**
 * Parses the given input into a 2D array
 * @param original the original input
 * @param repeat optional value to repeat the input map this often in every dimension
 */
private fun getInputMap(original: List<String>, repeat: Int = 1): Array<IntArray> {
    val originalWidth = (original.firstOrNull()?.length ?: 0)
    val originalHeight = original.size
    val originalMap = original.map { line -> line.map { it.toString().toByte() }.toByteArray() }.toTypedArray()
    val repeatedMap = Array(originalHeight * repeat) { IntArray(originalWidth * repeat) }
    for (x in 0 until originalWidth) {
        for (y in 0 until originalHeight) {
            repeatedMap[x][y] = originalMap[x][y].toInt()
            for (xFactor in 1 until repeat) {
                for (yFactor in 1 until repeat) {
                    repeatedMap[x + xFactor * originalWidth][y] = (originalMap[x][y].toInt() + xFactor).modulo(9)
                    repeatedMap[x][y + yFactor * originalHeight] = (originalMap[x][y].toInt() + yFactor).modulo(9)
                    repeatedMap[x + xFactor * originalWidth][y + yFactor * originalHeight] =
                        (originalMap[x][y].toInt() + xFactor + yFactor).modulo(9)
                }
            }
        }
    }
    return repeatedMap
}

private fun part1(input: List<String>) = getLowestRisk(getInputMap(input))

private fun part2(input: List<String>) = getLowestRisk(getInputMap(input, 5))

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
