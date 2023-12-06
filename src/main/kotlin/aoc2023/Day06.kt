package aoc2023

import multiplyOf
import readInput

private data class Race(val time: Long, val recordDistance: Long) {
    companion object {
        fun fromStrings(input: List<String>): List<Race> {
            val times =
                input.first().replace("Time: ", "").split("\\s+".toRegex()).filter { it.isNotEmpty() }
                    .map { it.toLong() }
            val distances =
                input.drop(1).first().replace("Distance: ", "").split("\\s+".toRegex()).filter { it.isNotEmpty() }
                    .map { it.toLong() }
            return times.withIndex().map { (index, time) -> Race(time, distances[index]) }
        }
    }

    val possiblePresstimesToBeatTheRecord = sequence {
        for (time in 0..time) {
            if (getAchievableDistance(time) > recordDistance) {
                yield(time)
            }
        }
    }

    private fun getAchievableDistance(pressedTime: Long) = (time - pressedTime) * pressedTime

}

object Day06 {
    fun part1(input: List<String>): Int {
        val races = Race.fromStrings(input)
        return races.map { it.possiblePresstimesToBeatTheRecord.count() }.multiplyOf { it }
    }

    fun part2(input: List<String>): Int {
        val time = input.first().replace("Time: ", "").replace(" ", "").toLong()
        val distance = input.drop(1).first().replace("Distance: ", "").replace(" ", "").toLong()
        val race = Race(time, distance)
        return race.possiblePresstimesToBeatTheRecord.count()
    }
}

fun main() {
    val testInput = readInput("Day06_test", 2023)
    check(Day06.part1(testInput) == 288)
    check(Day06.part2(testInput) == 71503)

    val input = readInput("Day06", 2023)
    println(Day06.part1(input))
    println(Day06.part2(input))
}
