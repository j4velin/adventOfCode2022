package aoc2023

import readInput

object Day11 {
	fun part1(input: List<String>): Int {
		return 0
	}

	fun part2(input: List<String>): Int {
		return 0
	}
}

fun main() {
	val testInput = readInput("Day11_test", 2023)
	check(Day11.part1(testInput) == 0)
	check(Day11.part2(testInput) == 0)

	val input = readInput("Day11", 2023)
	println(Day11.part1(input))
	println(Day11.part2(input))
}