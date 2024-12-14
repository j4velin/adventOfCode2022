package aoc2024

import readInput

object Day11 {

    /*
    private fun getStoneSize(stones:List<Long>, repeat:Int):Long {
        repeat(repeat) {
            val newArrangements = mutableListOf<Long>()
            stones.forEach { stone ->
                when {
                    stone == 0L -> newArrangements.add(1L)
                    stone.toString().length % 2 == 0 -> {
                        val stoneStr = stone.toString()
                        val middle = stoneStr.length / 2
                        newArrangements.add(stoneStr.substring(0, middle).toLong())
                        newArrangements.add(stoneStr.substring(middle).toLong())
                    }

                    else -> newArrangements.add(stone * 2024)
                }
            }
            stones = newArrangements
        }
        return stones.size
    }

     */

    private fun applyRules(stone: Long): List<Long> {
        return when {
            stone == 0L -> listOf(1)
            stone.toString().length % 2 == 0 -> {
                val stoneStr = stone.toString()
                val middle = stoneStr.length / 2
                listOf(stoneStr.substring(0, middle).toLong(), stoneStr.substring(middle).toLong())
            }

            else -> listOf(stone * 2024)
        }
    }

    fun part1(input: List<String>): Int {
        var stones = input.first().split(" ").map { it.toLong() }
        repeat(25) {
            stones = stones.flatMap { applyRules(it) }
        }
        return stones.size
    }

    fun part2(input: List<String>): Long {
        var stonesCount = input.first().split(" ").map { it.toLong() }.groupBy { it }.mapValues { it.value.size.toLong() }
        repeat(75) {
            val newCounts = mutableMapOf<Long, Long>()
            stonesCount.forEach { (stone, count) ->
                applyRules(stone).groupBy { it }.mapValues { it.value.size }
                    .forEach { newCounts[it.key] = (newCounts[it.key] ?: 0) + it.value * count }
            }
            stonesCount = newCounts
        }
        return stonesCount.values.sum()
    }
}

fun main() {
    val testInput = readInput("Day11_test", 2024)
    check(Day11.part1(testInput) == 55312)
    //check(Day11.part2(testInput) == 0)

    val input = readInput("Day11", 2024)
    println(Day11.part1(input))
    println(Day11.part2(input))
}
