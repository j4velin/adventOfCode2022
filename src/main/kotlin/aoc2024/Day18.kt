package aoc2024

import PointL
import dijkstra
import print
import readInput

object Day18 {
    fun part1(input: List<String>, grid: Pair<PointL, PointL>): Int {
        val bytes = input.map {
            val (x, y) = it.split(",").map { it.toLong() }
            PointL(x, y)
        }

        val (distance, path) = dijkstra(
            start = grid.first,
            withDiagonal = false,
            validGrid = grid,
            neighbourCondition = { _: PointL, next: PointL -> next !in bytes },
            targetCondition = { it == grid.second },
        )

        grid.print(mapOf('#' to bytes, 'O' to path), default = '.')

        return distance.toInt()
    }

    fun part2(input: List<String>, checkAfter: Int, grid: Pair<PointL, PointL>): PointL {

        val bytesAlreadySet = input.take(checkAfter).map {
            val (x, y) = it.split(",").map { it.toLong() }
            PointL(x, y)
        }.toMutableSet()

        val bytesToTest = input.drop(checkAfter + 1).map {
            val (x, y) = it.split(",").map { it.toLong() }
            PointL(x, y)
        }

        var firstBlockingByte: PointL? = null
        var currentIndex = 0
        while (firstBlockingByte == null) {
            // start searching from the first byte, which would block the current shortest path
            val (_, path) = dijkstra(
                start = grid.first,
                withDiagonal = false,
                validGrid = grid,
                neighbourCondition = { _: PointL, next: PointL -> next !in bytesAlreadySet },
                targetCondition = { it == grid.second },
            )

            if (path.isEmpty()) {
                firstBlockingByte = bytesToTest[currentIndex]
            } else {
                val nextIndex = bytesToTest.withIndex().drop(currentIndex).first { it.value in path }.index
                for (i in currentIndex..nextIndex) {
                    bytesAlreadySet.add(bytesToTest[i])
                }
                currentIndex = nextIndex
            }
        }

        return firstBlockingByte
    }
}

fun main() {
    val testInput = readInput("Day18_test", 2024)
    check(Day18.part1(testInput.take(12), PointL(0, 0) to PointL(6, 6)) == 22)
    check(Day18.part2(testInput, 12, PointL(0, 0) to PointL(6, 6)) == PointL(6, 1))

    val input = readInput("Day18", 2024)
    println(Day18.part1(input.take(1024), PointL(0, 0) to PointL(70, 70)))
    println(Day18.part2(input, 1024, PointL(0, 0) to PointL(70, 70)))
}
