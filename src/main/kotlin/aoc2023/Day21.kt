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
            parity: Parity = Parity.EVEN
        ): Set<PointL> {
            val reachable = currentPoints.flatMap {
                it.getNeighbours(validGrid = validGrid).filter { p -> map[p.x.toInt()][p.y.toInt()] != '#' }
            }.toSet()
            return if (remainingSteps == 0 || reachable.all { it in pointsReachedWithSameParitySoFar }) {
                pointsReachedWithSameParitySoFar
            } else {
                if ((remainingSteps % 2 == 1 && parity == Parity.EVEN) || (remainingSteps % 2 == 0 && parity == Parity.ODD)) {
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
                        currentPoints = setOf(bottom),
                        parity = Parity.from(stepsInNeighbourGridsUntilOriginalGridIsFilled)
                    ).size
                val pointsReachedInBottomGrid =
                    getReachablePointsForParity(
                        stepsInNeighbourGridsUntilOriginalGridIsFilled,
                        currentPoints = setOf(top),
                        parity = Parity.from(stepsInNeighbourGridsUntilOriginalGridIsFilled)
                    ).size
                val pointsReachedInLeftGrid =
                    getReachablePointsForParity(
                        stepsInNeighbourGridsUntilOriginalGridIsFilled,
                        currentPoints = setOf(right),
                        parity = Parity.from(stepsInNeighbourGridsUntilOriginalGridIsFilled)
                    ).size
                val pointsReachedInRightGrid =
                    getReachablePointsForParity(
                        stepsInNeighbourGridsUntilOriginalGridIsFilled,
                        currentPoints = setOf(left),
                        parity = Parity.from(stepsInNeighbourGridsUntilOriginalGridIsFilled)
                    ).size

                println("top: $pointsReachedInTopGrid, bottom: $pointsReachedInBottomGrid, left: $pointsReachedInLeftGrid, right: $pointsReachedInRightGrid")
                println("total: ${distances.size + pointsReachedInBottomGrid + pointsReachedInLeftGrid + pointsReachedInRightGrid + pointsReachedInTopGrid} in $maxDistance steps")

                // double the step count
                val nextFilledSteps = maxDistance * 2

                val bottomLeft = PointL(0, maxY)
                val bottomRight = PointL(maxX, maxY)
                val topLeft = PointL(0, 0)
                val topRight = PointL(maxX, 0)

                val stepsLeftWhenStartingFromCorners = nextFilledSteps - maxDistance

                val pointsReachedFromBottomLeft =
                    getReachablePointsForParity(
                        stepsLeftWhenStartingFromCorners,
                        currentPoints = setOf(bottomLeft),
                        parity = Parity.from(stepsLeftWhenStartingFromCorners)
                    ).size
                val pointsReachedFromBottomRight =
                    getReachablePointsForParity(
                        stepsLeftWhenStartingFromCorners,
                        currentPoints = setOf(bottomRight),
                        parity = Parity.from(stepsLeftWhenStartingFromCorners)
                    ).size
                val pointsReachedFromTopLeft =
                    getReachablePointsForParity(
                        stepsLeftWhenStartingFromCorners,
                        currentPoints = setOf(topLeft),
                        parity = Parity.from(stepsLeftWhenStartingFromCorners)
                    ).size
                val pointsReachedFromTopRight =
                    getReachablePointsForParity(
                        stepsLeftWhenStartingFromCorners,
                        currentPoints = setOf(topRight),
                        parity = Parity.from(stepsLeftWhenStartingFromCorners)
                    ).size

                val reached = distances.size * (4 + 1) + pointsReachedInBottomGrid + pointsReachedInLeftGrid +
                        pointsReachedInRightGrid + pointsReachedInTopGrid + pointsReachedFromBottomLeft +
                        pointsReachedFromBottomRight + pointsReachedFromTopLeft + pointsReachedFromTopRight

                println("total after $nextFilledSteps steps: $reached")

                val stepsForSmallDiamond = distances[top]!!
                val remainingSteps = maxSteps - stepsForSmallDiamond
                println(remainingSteps)

                val configurations: Map<Pair<Parity, PointL>, IntArray> = mapOf(
                    (Parity.EVEN to start) to IntArray(130),
                    (Parity.ODD to start) to IntArray(130),
                    (Parity.EVEN to top) to IntArray(130),
                    (Parity.ODD to top) to IntArray(130),
                    (Parity.EVEN to bottom) to IntArray(130),
                    (Parity.ODD to bottom) to IntArray(130),
                    (Parity.EVEN to left) to IntArray(130),
                    (Parity.ODD to left) to IntArray(130),
                    (Parity.EVEN to right) to IntArray(130),
                    (Parity.ODD to right) to IntArray(130),
                    (Parity.EVEN to topLeft) to IntArray(130),
                    (Parity.ODD to topLeft) to IntArray(130),
                    (Parity.EVEN to topRight) to IntArray(130),
                    (Parity.ODD to topRight) to IntArray(130),
                    (Parity.EVEN to bottomLeft) to IntArray(130),
                    (Parity.ODD to bottomLeft) to IntArray(130),
                    (Parity.EVEN to bottomRight) to IntArray(130),
                    (Parity.ODD to bottomRight) to IntArray(130),
                )
                configurations.forEach { entry ->
                    for (steps in 0..129) {
                        entry.value[steps] = getReachablePointsForParity(
                            steps,
                            currentPoints = setOf(topLeft),
                            parity = entry.key.first
                        ).size
                    }
                }

                var steps = 0
                var tileReached = 0L


                for (step in 1..maxSteps) {

                }

                steps += 130
                tileReached += configurations[Parity.EVEN to start]!![130] + configurations[Parity.ODD to top]!![65] +
                        configurations[Parity.ODD to bottom]!![65] + configurations[Parity.ODD to left]!![65] +
                        configurations[Parity.ODD to right]!![65]

                steps += 65
                tileReached += configurations[Parity.ODD to top]!![65] + configurations[Parity.ODD to bottom]!![65] +
                        configurations[Parity.ODD to left]!![65] + configurations[Parity.ODD to right]!![65]

                steps += 65


            }

            return 0
        }

        private enum class Parity {
            EVEN, ODD;

            companion object {
                fun from(input: Int) = if (input % 2 == 0) EVEN else ODD
            }
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
