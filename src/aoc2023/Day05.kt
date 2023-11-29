package aoc2023

import readInput

object Day05 {
	fun part1(input: List<String>): Int {
		return 0
	}

	fun part2(input: List<String>): Int {
		return 0
	}
}

fun main() {
	val testInput = readInput("Day05_test", 2023)
	check(Day05.part1(testInput) == 0)
	check(Day05.part2(testInput) == 0)

	val input = readInput("Day05", 2023)
	println(Day05.part1(input))
	println(Day05.part2(input))
}
