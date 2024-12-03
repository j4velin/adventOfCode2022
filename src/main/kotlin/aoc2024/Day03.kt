package aoc2024

import readInput

object Day03 {

    fun part1(input: String): Int {
        val regex = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
        return regex.findAll(input)
            .map { it.groups[1]!!.value.toInt() to it.groups[2]!!.value.toInt() }
            .sumOf { it.first * it.second }
    }

    fun part2(input: String): Int {
        val regex = """(mul\((\d{1,3}),(\d{1,3})\)|do\(\)|don't\(\))""".toRegex()
        var enabled = true
        return regex.findAll(input).mapNotNull {
            when (it.value) {
                "do()" -> enabled = true
                "don't()" -> enabled = false
                else -> if (enabled) {
                    return@mapNotNull it
                }
            }
            null
        }.map { it.groups[2]!!.value.toInt() to it.groups[3]!!.value.toInt() }.sumOf { it.first * it.second }
    }
}

fun main() {
    check(Day03.part1("""xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))""") == 161)
    check(Day03.part2("""xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))""") == 48)

    val input = readInput("Day03", 2024).joinToString(separator = "")
    println(Day03.part1(input))
    println(Day03.part2(input))
}
