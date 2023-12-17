package aoc2023

import Point
import readInput
import to2dIntArray
import java.util.*

object Day17 {

    private data class StraightCount(val horizontal: Int, val vertical: Int) {
        val nextHorizontal by lazy { StraightCount(horizontal + 1, 0) }
        val nextVertical by lazy { StraightCount(0, vertical + 1) }
    }

    private data class State(
        val consecutiveStraight: StraightCount = StraightCount(0, 0),
        val position: Point,
        val distance: Int = 0,
        val comingFrom: Point? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (consecutiveStraight != other.consecutiveStraight) return false
            if (position != other.position) return false

            return true
        }

        override fun hashCode(): Int {
            var result = consecutiveStraight.hashCode()
            result = 31 * result + position.hashCode()
            return result
        }
    }

    private fun solve(input: List<String>, minConsecutive: Int = 0, maxConsecutive: Int): Int {
        val map = input.to2dIntArray()
        val maxX = input.size - 1
        val maxY = input.first().length - 1
        val start = Point(0, 0)
        val end = Point(maxX, maxY)
        val validGrid = start to end
        val shortestDistances = mutableMapOf<Point, MutableList<State>>()
        val queue: Queue<State> = PriorityQueue { o1, o2 -> o1.distance.compareTo(o2.distance) }
        val startState = State(position = start)
        queue.add(startState)
        shortestDistances[start] = mutableListOf(startState)
        while (queue.isNotEmpty()
            && shortestDistances[end]?.none { it.consecutiveStraight.horizontal >= minConsecutive || it.consecutiveStraight.vertical >= minConsecutive } != false
        ) {
            val current = queue.poll()
            current.position.getNeighbours(validGrid = validGrid).mapNotNull { nextPosition ->
                val newDistance = current.distance + map[nextPosition.x][nextPosition.y]
                if (current.comingFrom == nextPosition) {
                    null
                } else if (current.position.x == nextPosition.x && current.consecutiveStraight.vertical < maxConsecutive
                    && (current.comingFrom == null || current.comingFrom.x == current.position.x || current.consecutiveStraight.horizontal >= minConsecutive)
                ) {
                    State(current.consecutiveStraight.nextVertical, nextPosition, newDistance, current.position)
                } else if (current.position.y == nextPosition.y && current.consecutiveStraight.horizontal < maxConsecutive
                    && (current.comingFrom == null || current.comingFrom.y == current.position.y || current.consecutiveStraight.vertical >= minConsecutive)
                ) {
                    State(current.consecutiveStraight.nextHorizontal, nextPosition, newDistance, current.position)
                } else {
                    null
                }
            }.forEach { nextState ->
                val list = shortestDistances[nextState.position]
                    ?: mutableListOf<State>().also { shortestDistances[nextState.position] = it }

                if (!list.contains(nextState)) {
                    list.add(nextState)
                    queue.add(nextState)
                }
            }
        }

        return shortestDistances[end]
            ?.filter { it.consecutiveStraight.horizontal >= minConsecutive || it.consecutiveStraight.vertical >= minConsecutive }
            ?.minOfOrNull { it.distance }
            ?: throw IllegalStateException("Could not find any path to $end")
    }

    fun part1(input: List<String>) = solve(input, maxConsecutive = 3)

    fun part2(input: List<String>) = solve(input, maxConsecutive = 10, minConsecutive = 4)
}

fun main() {
    val testInput = readInput("Day17_test", 2023)
    check(Day17.part1(testInput) == 102)
    check(Day17.part2(testInput) == 94)

    val input = readInput("Day17", 2023)
    println(Day17.part1(input))
    println(Day17.part2(input))
}
