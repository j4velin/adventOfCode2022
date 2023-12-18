package aoc2023

import Point
import readInput

@OptIn(ExperimentalStdlibApi::class)
object Day18 {
    fun part1(input: List<String>): Int {
        var current = Point(0, 0)
        val dugOut = mutableSetOf(current)
        input.forEach { line ->
            val split = line.split(" ")
            val direction = split[0]
            val length = split[1].toInt()
            val delta = when (direction) {
                "U" -> Point(0, -1)
                "D" -> Point(0, 1)
                "L" -> Point(-1, 0)
                "R" -> Point(1, 0)
                else -> throw IllegalArgumentException("Unknown direction: $direction")
            }
            repeat(length) {
                current += delta
                dugOut.add(current)
            }
        }
        val maxX = dugOut.maxOf { it.x }
        val maxY = dugOut.maxOf { it.y }
        val minX = dugOut.minOf { it.x }
        val minY = dugOut.minOf { it.y }
        val width = maxX - minX
        val height = maxY - minY
        val map = Array(width + 1) { CharArray(height + 1) { '.' } }
        dugOut.forEach {
            map[it.x - minX][it.y - minY] = '#'
        }

        val grid = Point(0, 0) to Point(width, height)
        var changed = true
        while (changed) {
            changed = false
            for (x in 0..width) {
                for (y in 0..height) {
                    val value = map[x][y]
                    if (value == '.') {
                        val point = Point(x, y)
                        val neighbours = point.getNeighbours().filter { it.isWithin(grid) }.map { map[it.x][it.y] }
                        if (neighbours.size < 4 || neighbours.any { it == ' ' }) {
                            map[x][y] = ' '
                            changed = true
                        }
                    }
                }
            }
        }

        return map.sumOf { column -> column.count { it != ' ' } }
    }

    fun part2(input: List<String>): Long {
        var current = Point(0, 0)
        val dugOut = mutableListOf(current)
        input.forEach { line ->
            // drop '(#' and ')'
            val color = line.substring(line.lastIndexOf('(')).drop(2).take(6)
            val direction = color.last()
            val length = color.take(5).hexToInt()
            // 0 means R, 1 means D, 2 means L, and 3 means U.
            current += when (direction) {
                '3' -> Point(0, -length)
                '1' -> Point(0, length)
                '2' -> Point(-length, 0)
                '0' -> Point(length, 0)
                else -> throw IllegalArgumentException("Unknown direction: $direction")
            }
            dugOut.add(current)
        }

        val maxX = dugOut.maxOf { it.x }
        val maxY = dugOut.maxOf { it.y }
        val minX = dugOut.minOf { it.x }
        val minY = dugOut.minOf { it.y }
        val width = maxX - minX
        val height = maxY - minY

        val total = width.toLong() * height.toLong()

        println("$width x $height -> $total")

        return 0L
    }
}

fun main() {
    val testInput = readInput("Day18_test", 2023)
    check(Day18.part1(testInput) == 62)
    check(Day18.part2(testInput) == 952408144115L)

    val input = readInput("Day18", 2023)
    println(Day18.part1(input))
    println(Day18.part2(input))
}
