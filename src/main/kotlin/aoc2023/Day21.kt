package aoc2023

import PointL
import modulo
import readInput
import to2dCharArray

object Day21 {

    private class GardenMap(private val map: Array<CharArray>) {
        private val maxX = map.size - 1
        private val maxY = map.first().size - 1
        private val validGrid = PointL(0, 0) to PointL(maxX, maxY)
        private val start = map.withIndex()
            .flatMap { (x, column) -> column.withIndex().map { (y, char) -> PointL(x, y) to char } }
            .first { it.second == 'S' }.first

        tailrec fun getReachablePoints(
            remainingSteps: Int,
            currentPoints: Set<PointL> = setOf(start),
            pointsReachedWithEvenNumberOfStepsSoFar: MutableSet<PointL> = mutableSetOf(start)
        ): Set<PointL> {
            val reachable = currentPoints.flatMap {
                it.getNeighbours(validGrid = validGrid).filter { p -> map[p.x.toInt()][p.y.toInt()] != '#' }
            }.toSet()
            return if (remainingSteps == 0 || reachable.all { it in pointsReachedWithEvenNumberOfStepsSoFar }) {
                pointsReachedWithEvenNumberOfStepsSoFar
            } else {
                if (remainingSteps % 2 == 1) {
                    // we're at an "odd" position, so all points reachable from here can be reached with an even amount of steps
                    pointsReachedWithEvenNumberOfStepsSoFar.addAll(reachable)
                }
                getReachablePoints(remainingSteps - 1, reachable, pointsReachedWithEvenNumberOfStepsSoFar)
            }
        }
    }


    fun part1(input: List<String>, steps: Int): Int {
        val map = GardenMap(input.to2dCharArray())
        return map.getReachablePoints(steps).size
    }

    fun part2(input: List<String>, steps: Int): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day21_test", 2023)
    check(Day21.part1(testInput, steps = 6) == 16)
    //check(Day21.part2(testInput, steps = 10) == 50)
    //check(Day21.part2(testInput, steps = 50) == 1594)

    val input = readInput("Day21", 2023)
    println(Day21.part1(input, steps = 64))
    println(Day21.part2(input, steps = 26501365))
}
