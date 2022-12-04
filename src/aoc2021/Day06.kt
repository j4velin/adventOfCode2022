package aoc2021

import readInput

private class FishPopulation(input: List<String>) {

    // every slot in the array corresponds to one day in the fish cycle
    private val fishes = LongArray(9)
    private var currentDay = 0

    init {
        input.first().split(",").map { it.toInt() }.forEach { fishes[it]++ }
    }

    fun advanceDay() {
        val currentSlot = currentDay % fishes.size
        // current fishes cycle is reset to 6 'after another day' -> 7 today
        fishes[(currentSlot + 7) % fishes.size] += fishes[currentSlot]
        // newborn fishes are set to 8 from tomorrow -> 9 today -> currentSlot again, so we simply keep the current
        // value at the current slot to represent those new baby fish
        currentDay++
    }

    fun getTotalPopulation() = fishes.sum()
}

private fun part1(input: List<String>): Long {
    val population = FishPopulation(input)
    repeat(80) { population.advanceDay() }
    return population.getTotalPopulation()
}

private fun part2(input: List<String>): Long {
    val population = FishPopulation(input)
    repeat(256) { population.advanceDay() }
    return population.getTotalPopulation()
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539L)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
