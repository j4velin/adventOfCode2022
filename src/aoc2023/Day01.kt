package aoc2023

import readInput

object Day01 {
	fun part1(input: List<String>): Int {
		return 0
	}

	fun part2(input: List<String>): Int {
		return 0
	}
}

fun main() {
	val testInput = readInput("Day01_test", 2023)
	check(Day01.part1(testInput) == 0)
	check(Day01.part2(testInput) == 0)

	val input = readInput("Day01", 2023)
	println(Day01.part1(input))
	println(Day01.part2(input))
}
