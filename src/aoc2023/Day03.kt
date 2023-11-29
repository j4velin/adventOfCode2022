package aoc2023

import readInput

object Day03 {
	fun part1(input: List<String>): Int {
		return 0
	}

	fun part2(input: List<String>): Int {
		return 0
	}
}

fun main() {
	val testInput = readInput("Day03_test", 2023)
	check(Day03.part1(testInput) == 0)
	check(Day03.part2(testInput) == 0)

	val input = readInput("Day03", 2023)
	println(Day03.part1(input))
	println(Day03.part2(input))
}
