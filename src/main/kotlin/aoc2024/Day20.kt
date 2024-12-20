package aoc2024

import PointL
import dijkstra
import find
import findAll
import grid
import get
import readInput
import to2dCharArray

object Day20 {

    private data class Cheat(val start: PointL, val end: PointL)

    fun part1(input: List<String>): Map<Long, Int> = findCheats(input, 2)

    fun part2(input: List<String>): Map<Long, Int> = findCheats(input, 20)

    private fun findCheats(input: List<String>, allowedCheatTime: Int): Map<Long, Int> {
        val map = input.to2dCharArray()
        val start = map.find('S') ?: throw IllegalArgumentException("no start found")
        val end = map.find('E') ?: throw IllegalArgumentException("no end found")

        val cacheToEnd = mutableMapOf(end to 0L)
        val cacheFromStart = mutableMapOf(start to 0L)

        val shortestPathWithoutCheat = dijkstra(
            start = start,
            validGrid = map.grid,
            neighbourCondition = { _, next -> map.get(next.x, next.y) != '#' },
            targetCondition = { it == end })

        val shortestDistanceWithoutCheat = shortestPathWithoutCheat.first

        shortestPathWithoutCheat.second.withIndex().forEach { (distance, point) ->
            cacheFromStart[point] = distance + 1L
        }

        shortestPathWithoutCheat.second.reversed().withIndex().forEach { (distance, point) ->
            cacheToEnd[point] = distance.toLong()
        }

        cacheToEnd[start] = shortestDistanceWithoutCheat

        val trackPoints = map.findAll("SE.".toCharArray()).keys

        val groupedBySavingTimes = trackPoints.flatMap { point ->
            trackPoints.filter { point.distanceTo(it) <= allowedCheatTime }.map { exit ->
                val distanceFromStart = cacheFromStart[point]!!
                val distanceThroughWalls = point.distanceTo(exit)
                val distanceToEnd = cacheToEnd[exit]!!

                val totalDistance = distanceFromStart + distanceThroughWalls + distanceToEnd
                val cheat = Cheat(point, exit)

                cheat to totalDistance
            }
        }.filter { it.second < shortestDistanceWithoutCheat }
            .distinctBy { it.first }
            .groupBy { shortestDistanceWithoutCheat - it.second }
            .mapValues { it.value.size }

        return groupedBySavingTimes
    }
}

fun main() {
    val testInput = readInput("Day20_test", 2024)
    check(Day20.part1(testInput).values.sum() == 44)
    check(Day20.part2(testInput).filter { it.key >= 50 }.values.sum() == 285)

    val input = readInput("Day20", 2024)
    println(Day20.part1(input).filter { it.key >= 100 }.values.sum())
    println(Day20.part2(input).filter { it.key >= 100 }.values.sum())
}
