package aoc2022

import Point
import readInput
import kotlin.math.max

private enum class RockShape(val points: Collection<Point>) {
    Minus(setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0))),
    Plus(setOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2))),
    L(setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2, 2))),
    I(setOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3))),
    Block(setOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1)));

    companion object {
        val sequence = sequence {
            while (true) {
                yield(Minus)
                yield(Plus)
                yield(L)
                yield(I)
                yield(Block)
            }
        }
    }
}

private class Rock(val shape: RockShape, top: Int) {

    var points = shape.points.map { it + Point(2, top + 3) }
        private set

    fun moveLeft(blocked: Collection<Point>) {
        val tmp = points.map { it.move(-1, 0) }
        if (tmp.all { it.x >= 0 && !blocked.contains(it) }) {
            this.points = tmp
        }
    }

    fun moveRight(blocked: Collection<Point>) {
        val tmp = points.map { it.move(1, 0) }
        if (tmp.all { it.x < 7 && !blocked.contains(it) }) {
            this.points = tmp
        }
    }

    fun moveDown(blocked: Collection<Point>): Boolean {
        val tmp = points.map { it.move(0, -1) }
        val allowed = tmp.all { it.y >= 0 && !blocked.contains(it) }
        if (allowed) {
            this.points = tmp
        }
        return allowed
    }
}


private data class Pattern(val jetIndex: Int, val rockShape: RockShape, val topPoints: List<Int>) {
    var offset = 0L
    var rocks = 0L
}

private fun getHeight(input: List<String>, maxRocks: Long): Long {
    val jetIterator = sequence {
        while (true) {
            yieldAll(input.first().toCharArray().withIndex().toList())
        }
    }.iterator()
    val topPoints = IntArray(7) { -1 }
    var blockedPoints = mutableSetOf<Point>()
    var offset = 0L
    var rocks = 0L
    var missingRocks = maxRocks
    val patterns = mutableSetOf<Pattern>()
    var patternFound = false
    RockShape.sequence.takeWhile { missingRocks > 0 }.forEach { shape ->
        missingRocks--
        rocks++
        val rock = Rock(shape, topPoints.max() + 1)
        var moved = true
        var jetIndex = -1
        while (moved) {
            val nextJet = jetIterator.next()
            jetIndex = nextJet.index
            if (nextJet.value == '>') {
                rock.moveRight(blockedPoints)
            } else {
                rock.moveLeft(blockedPoints)
            }
            moved = rock.moveDown(blockedPoints)
        }
        blockedPoints.addAll(rock.points)
        for (p in rock.points) {
            topPoints[p.x] = max(topPoints[p.x], p.y)
        }
        if (!patternFound) {
            val min = topPoints.min()
            if (min > 2) {
                val pattern = Pattern(jetIndex, rock.shape, topPoints.clone().asList())
                pattern.offset = offset
                pattern.rocks = rocks
                if (!patterns.add(pattern)) {
                    val existing = patterns.find { it == pattern }!!
                    val rocksDiff = rocks - existing.rocks
                    val offsetDiff = offset - existing.offset
                    val repeats = (missingRocks) / rocksDiff
                    missingRocks -= (rocksDiff * repeats)
                    offset += offsetDiff * repeats
                    patternFound = true
                } else {
                    offset += min
                    for (i in topPoints.indices) {
                        topPoints[i] = topPoints[i] - min
                    }
                    blockedPoints = blockedPoints.map { it.move(0, -min) }.filter { it.y >= 0 }.toMutableSet()
                }
            }
        }
    }
    return topPoints.max() + offset + 1
}

private fun part1(input: List<String>) = getHeight(input, 2022L).toInt()

private fun part2(input: List<String>) = getHeight(input, 1000000000000L)

fun main() {
    val testInput = readInput("Day17_test", 2022)
    check(part1(testInput) == 3068)
    check(part2(testInput) == 1514285714288L)
    val input = readInput("Day17", 2022)
    println(part1(input))
    println(part2(input))

}