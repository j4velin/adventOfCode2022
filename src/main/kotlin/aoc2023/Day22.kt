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

        fun moveDown(dz: Int) = Brick(start.move(0, 0, -dz), end.move(0, 0, -dz))

        fun laysOnAny(other: Set<Brick>) = other.any { otherBrick ->
            xRange.overlaps(otherBrick.xRange) && yRange.overlaps(otherBrick.yRange) && zRange.first == otherBrick.zRange.last + 1
        }

        fun supports(other: Brick) =
            xRange.overlaps(other.xRange) && yRange.overlaps(other.yRange) && zRange.last == other.zRange.first - 1
    }

    fun part1(input: List<String>): Int {
        val fallingBricks = input.map { Brick.fromString(it) }
        val bricksInFinalPosition = mutableSetOf<Brick>()

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

        val bricksSafeToRemove = bricksInFinalPosition.filter { brick ->
            val bricksSupportedByTheCurrentBrick =
                bricksInFinalPosition.filter { it.zRange.first == brick.zRange.last + 1 }.filter { brick.supports(it) }
            val possibleOtherSupports =
                bricksInFinalPosition.filter { it != brick && it.zRange.last == brick.zRange.last }

            val unsupportedBricks = bricksSupportedByTheCurrentBrick.filter { brickToTest ->
                possibleOtherSupports.none { support -> support.supports(brickToTest) }
            }

            unsupportedBricks.isEmpty()
        }

        return bricksSafeToRemove.size
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day22_test", 2023)
    check(Day22.part1(testInput) == 5)
    check(Day22.part2(testInput) == 0)

    val input = readInput("Day22", 2023)
    println(Day22.part1(input))
    println(Day22.part2(input))
}
