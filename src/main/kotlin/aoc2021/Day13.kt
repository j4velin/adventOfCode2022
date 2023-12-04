package aoc2021

import readInput
import kotlin.math.max

private fun Boolean.toInt() = if (this) 1 else 0

/**
 * Folds the transparent input page according to the given instructions
 *
 * @param array the page to aoc2021.fold
 * @param foldInstruction folding instruction in the form of 'aoc2021.fold along x=42'
 * @return the folded transparent page with all the points marked on each side of the folding line overlayed with each other
 */
private fun fold(array: Array<BooleanArray>, foldInstruction: String): Array<BooleanArray> {
    val split = foldInstruction.removePrefix("aoc2021.fold along ").split("=")
    val foldAxis = split[0]
    val foldValue = split[1].toInt()
    val result = if (foldAxis == "y") {
        Array(array.size) { BooleanArray(foldValue + 1) }
    } else {
        Array(foldValue + 1) { BooleanArray(array.firstOrNull()?.size ?: 0) }
    }
    for (x in array.indices) {
        for (y in 0 until (array.firstOrNull()?.size ?: 0)) {
            if ((foldAxis == "x" && x <= foldValue) or (foldAxis == "y" && y <= foldValue)) {
                result[x][y] = array[x][y]
            } else if (foldAxis == "x") {
                val newX = max(0, 2 * foldValue - x)
                result[newX][y] = result[newX][y] or array[x][y]
            } else {
                val newY = max(0, 2 * foldValue - y)
                result[x][newY] = result[x][newY] or array[x][y]
            }
        }
    }
    return result
}

/**
 * Parses the input string of the initial points into a 2D array
 */
private fun parseArray(input: List<String>): Array<BooleanArray> {
    val maxX = input.maxOf { it.split(",")[0].toInt() }
    val maxY = input.maxOf { it.split(",")[1].toInt() }
    val result = Array(maxX + 1) { BooleanArray(maxY + 1) }
    input.map { line -> line.split(",").map { it.toInt() } }.forEach { result[it[0]][it[1]] = true }
    return result
}

private fun part1(input: List<String>): Int {
    val initial = parseArray(input.takeWhile { it.isNotEmpty() })
    val instruction = input.dropWhile { it.isNotBlank() }.drop(1).first()
    return fold(initial, instruction).sumOf { x ->
        x.sumOf { y -> y.toInt() }
    }
}

private fun part2(input: List<String>): String {
    var current = parseArray(input.takeWhile { it.isNotEmpty() })
    val instructions = input.dropWhile { it.isNotBlank() }.drop(1)
    instructions.forEach { current = fold(current, it) }
    return buildString {
        for (y in 0 until (current.firstOrNull()?.size ?: 0)) {
            for (x in current.indices) {
                append(if (current[x][y]) "#" else ".")
            }
            appendLine()
        }
    }
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
