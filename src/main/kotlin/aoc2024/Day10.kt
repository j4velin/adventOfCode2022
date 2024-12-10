package aoc2024

import PointL
import bfs
import findAll
import get
import readInput
import to2dCharArray

typealias Path = List<PointL>

object Day10 {

    private fun getScores(input: List<String>, scoreFunction: (List<Path>) -> Int): List<Int> {
        val map = input.to2dCharArray()
        val startingPoints = map.findAll("0".toCharArray()).keys
        val scores = startingPoints.map {
            val paths = map.bfs(
                start = it,
                withDiagonal = false,
                neighbourCondition = { current, next ->
                    map.get(current.x, current.y).digitToInt() + 1 == map.get(next.x, next.y).digitToInt()
                }, targetCondition = { current ->
                    map.get(current.x, current.y) == '9'
                })
            scoreFunction(paths)
        }
        return scores
    }

    fun part1(input: List<String>): Int {
        return getScores(input) { it.map { it.last() }.distinct().size }.sum()
    }

    fun part2(input: List<String>): Int {
        return getScores(input) { it.size }.sum()
    }
}

fun main() {
    val testInput = readInput("Day10_test", 2024)
    check(Day10.part1(testInput) == 36)
    check(Day10.part2(testInput) == 81)

    val input = readInput("Day10", 2024)
    println(Day10.part1(input))
    println(Day10.part2(input))
}
