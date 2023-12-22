package aoc2023

import Point3
import overlaps
import readInput
import kotlin.math.max
import kotlin.math.min

object Day22 {

    private data class Brick(private val start: Point3, private val end: Point3) {

        val xRange = min(start.x, end.x)..max(start.x, end.x)
        val yRange = min(start.y, end.y)..max(start.y, end.y)
        val zRange = min(start.z, end.z)..max(start.z, end.z)

        companion object {
            fun fromString(input: String): Brick {
                val split = input.split("~")
                return Brick(Point3.fromCsvString(split.first()), Point3.fromCsvString(split.last()))
            }
        }

        /**
         * Move the brick downwards by the given delta
         * @param dz the distance to move in z direction
         */
        fun moveDown(dz: Int) = Brick(start.move(0, 0, -dz), end.move(0, 0, -dz))

        /**
         * @return true, if this brick lays directly on any of the bricks given in [other]
         */
        fun laysOnAny(other: Set<Brick>) = other.any { otherBrick ->
            xRange.overlaps(otherBrick.xRange) && yRange.overlaps(otherBrick.yRange) && zRange.first == otherBrick.zRange.last + 1
        }

        /**
         * @return true, if this brick supports the [other] brick from falling down
         */
        fun supports(other: Brick) =
            xRange.overlaps(other.xRange) && yRange.overlaps(other.yRange) && zRange.last == other.zRange.first - 1
    }

    private fun getBricksInFinalPosition(input: List<String>): Set<Brick> {
        val fallingBricks = input.map { Brick.fromString(it) }
        val bricksInFinalPosition = mutableSetOf<Brick>()

        // fall down
        fallingBricks.sortedBy { it.zRange.first }.forEach { brick ->
            if (brick.zRange.first == 1L) {
                bricksInFinalPosition.add(brick)
            } else {
                var tmpBrick = brick
                while (!tmpBrick.laysOnAny(bricksInFinalPosition) && tmpBrick.zRange.first > 1) {
                    tmpBrick = tmpBrick.moveDown(1)
                }
                bricksInFinalPosition.add(tmpBrick)
            }
        }
        return bricksInFinalPosition
    }

    /**
     * @return bricks, which are only supported by [toTest] (e.g. which would fall down if [toTest] was removed)
     */
    private fun getDependantBricks(toTest: Brick, bricks: Collection<Brick>): Collection<Brick> {
        val bricksSupportedByTheCurrentBrick =
            bricks.filter { it.zRange.first == toTest.zRange.last + 1 }.filter { toTest.supports(it) }
        val possibleOtherSupports =
            bricks.filter { it != toTest && it.zRange.last == toTest.zRange.last }

        val unsupportedBricks = bricksSupportedByTheCurrentBrick.filter { brickToTest ->
            possibleOtherSupports.none { support -> support.supports(brickToTest) }
        }
        return unsupportedBricks
    }

    /**
     * Tests, how many bricks would fall when removing a set of bricks.
     *
     * @param removedBricks the bricks to remove
     * @param remainingBricks the bricks, which are still standing (all bricks except those in [removedBricks])
     * @return the number of bricks, which would fall further down if the bricks in [removedBricks] are removed
     */
    private fun wouldFall(removedBricks: Set<Brick>, remainingBricks: Collection<Brick>): Int {
        val unsupportedBricks =
            remainingBricks.filter { brick -> brick.zRange.first > 1 && remainingBricks.none { it.supports(brick) } }
        return if (unsupportedBricks.isNotEmpty()) {
            wouldFall(removedBricks + unsupportedBricks, remainingBricks.filter { it !in removedBricks })
        } else {
            removedBricks.size
        }
    }

    fun part1(input: List<String>): Int {
        val allBricks = getBricksInFinalPosition(input)
        val bricksSafeToRemove = allBricks.filter { brick -> getDependantBricks(brick, allBricks).isEmpty() }
        return bricksSafeToRemove.size
    }

    fun part2(input: List<String>): Int {
        val allBricks = getBricksInFinalPosition(input)
        val bricksUnSafeToRemove = allBricks.filter { brick -> getDependantBricks(brick, allBricks).isNotEmpty() }

        // fast enough for the puzzle input, no need to optimize
        return bricksUnSafeToRemove.parallelStream().map { brickToRemove ->
            // -1 because we must not count the 'brickToRemove' itself
            wouldFall(setOf(brickToRemove), allBricks.filter { it != brickToRemove }) - 1
        }.reduce(0) { a, b -> a + b }
    }
}

fun main() {
    val testInput = readInput("Day22_test", 2023)
    check(Day22.part1(testInput) == 5)
    check(Day22.part2(testInput) == 7)

    val input = readInput("Day22", 2023)
    println(Day22.part1(input))
    println(Day22.part2(input))
}
