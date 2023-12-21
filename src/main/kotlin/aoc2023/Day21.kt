package aoc2023

import PointL
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

        tailrec fun getReachablePointsForParity(
            remainingSteps: Int,
            currentPoints: Set<PointL> = setOf(start),
            pointsReachedWithSameParitySoFar: MutableSet<PointL> = currentPoints.toMutableSet(),
            parityEven: Boolean = true
        ): Set<PointL> {
            val reachable = currentPoints.flatMap {
                it.getNeighbours(validGrid = validGrid).filter { p -> map[p.x.toInt()][p.y.toInt()] != '#' }
            }.toSet()
            return if (remainingSteps == 0 || reachable.all { it in pointsReachedWithSameParitySoFar }) {
                pointsReachedWithSameParitySoFar
            } else {
                if ((remainingSteps % 2 == 1 && parityEven) || (remainingSteps % 2 == 0 && !parityEven)) {
                    // we're at an "odd" position, so all points reachable from here can be reached with an even amount of steps
                    pointsReachedWithSameParitySoFar.addAll(reachable)
                }
                getReachablePointsForParity(
                    remainingSteps - 1,
                    reachable,
                    pointsReachedWithSameParitySoFar
                )
            }
        }

        fun part2(maxSteps: Int): Int {
            val distances = visit(setOf(start), mutableMapOf(), 0)
            val maxDistance = distances.maxOf { it.value }
            val maxDistancePoints = distances.filter { it.value == maxDistance }.map { it.key }
            val maxDistanceAtTheEdges =
                maxDistancePoints.all { (it.x == 0L || it.x.toInt() == maxX) && (it.y == 0L || it.y.toInt() == maxY) }
            val top = PointL(start.x, maxY.toLong())
            val bottom = PointL(start.x, 0L)
            val left = PointL(0, start.y)
            val right = PointL(maxX.toLong(), start.y)
            val sameDistanceToAllSides = listOf(left, right, top, bottom).mapNotNull { distances[it] }
            val totalEmptyTiles = map.sumOf { it.count { c -> c != '#' } }
            val stepsInNeighbourGridsUntilOriginalGridIsFilled = maxDistance - sameDistanceToAllSides.first()

            if (maxDistanceAtTheEdges) {
                println("max distance at the edges: $maxDistance")
                println("distance to all sides: ${sameDistanceToAllSides.first()}")
                println("visited in original grid: ${distances.size}")
                println("total empty tiles in original grid: $totalEmptyTiles")
                println("unreachable tiles: ${totalEmptyTiles - distances.size}")
                println("stepsInNeighbourGridsUntilOriginalGridIsFilled: $stepsInNeighbourGridsUntilOriginalGridIsFilled")
                println("nextMiddleReached: ${stepsInNeighbourGridsUntilOriginalGridIsFilled > sameDistanceToAllSides.first()}")

                val pointsReachedInTopGrid =
                    getReachablePointsForParity(
                        stepsInNeighbourGridsUntilOriginalGridIsFilled,
                        currentPoints = setOf(top),
                        parityEven = stepsInNeighbourGridsUntilOriginalGridIsFilled % 2 == 0
                    ).size
                val pointsReachedInBottomGrid =
                    getReachablePointsForParity(
                        stepsInNeighbourGridsUntilOriginalGridIsFilled,
                        currentPoints = setOf(bottom),
                        parityEven = stepsInNeighbourGridsUntilOriginalGridIsFilled % 2 == 0
                    ).size
                val pointsReachedInLeftGrid =
                    getReachablePointsForParity(
                        stepsInNeighbourGridsUntilOriginalGridIsFilled,
                        currentPoints = setOf(left),
                        parityEven = stepsInNeighbourGridsUntilOriginalGridIsFilled % 2 == 0
                    ).size
                val pointsReachedInRightGrid =
                    getReachablePointsForParity(
                        stepsInNeighbourGridsUntilOriginalGridIsFilled,
                        currentPoints = setOf(right),
                        parityEven = stepsInNeighbourGridsUntilOriginalGridIsFilled % 2 == 0
                    ).size

                println("top: $pointsReachedInTopGrid, bottom: $pointsReachedInBottomGrid, left: $pointsReachedInLeftGrid, right: $pointsReachedInRightGrid")
                println("total: ${distances.size + pointsReachedInBottomGrid + pointsReachedInLeftGrid + pointsReachedInRightGrid + pointsReachedInTopGrid} in $maxDistance steps")

                val nextFilledSteps = maxDistance * 2
                val reached =
                    distances.size * 4 + pointsReachedInBottomGrid + pointsReachedInLeftGrid + pointsReachedInRightGrid + pointsReachedInTopGrid

                println("total after $nextFilledSteps steps: $reached")

                val repeat = maxSteps / maxDistance
                val repeatReached =
                    distances.size.toLong() * repeat * repeat + pointsReachedInBottomGrid + pointsReachedInLeftGrid + pointsReachedInRightGrid + pointsReachedInTopGrid

                println("repeat $repeat times -> ${repeat * maxDistance} steps -> $repeatReached")

                val remainingSteps = maxSteps - repeat * maxDistance

                println("remaining: $remainingSteps steps")

            }

            return 0
        }

        tailrec fun visit(current: Set<PointL>, alreadySeen: MutableMap<PointL, Int>, steps: Int): Map<PointL, Int> {
            val newPoints = current.flatMap { it.getNeighbours(validGrid = validGrid) }
                .filter { p -> map[p.x.toInt()][p.y.toInt()] != '#' && p !in alreadySeen.keys }.toSet()
            return if (newPoints.isEmpty()) {
                alreadySeen
            } else {
                newPoints.forEach { alreadySeen[it] = steps }
                visit(newPoints, alreadySeen, steps + 1)
            }
        }
    }


    fun part1(input: List<String>, steps: Int): Int {
        val map = GardenMap(input.to2dCharArray())
        return map.getReachablePointsForParity(steps).size
    }

    fun part2(input: List<String>, steps: Int): Int {
        val map = GardenMap(input.to2dCharArray())
        return map.part2(steps) / 2
    }
}

fun main() {
    val testInput = readInput("Day21_test", 2023)
    check(Day21.part1(testInput, steps = 6) == 16)
    /*
    check(Day21.part2(testInput, steps = 10) == 50)
    check(Day21.part2(testInput, steps = 50) == 1594)
    check(Day21.part2(testInput, steps = 100) == 6536)
    check(Day21.part2(testInput, steps = 500) == 167004)
    check(Day21.part2(testInput, steps = 1000) == 668697)
    check(Day21.part2(testInput, steps = 5000) == 16733044)

     */

    val input = readInput("Day21", 2023)
    println(Day21.part1(input, steps = 64))
    println(Day21.part2(input, steps = 26501365))
}
