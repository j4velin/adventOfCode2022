package aoc2024

import modulo
import readInput

object Day22 {

    private infix fun Long.mix(input: Long) = this xor input

    private fun Long.prune() = this.modulo(16777216L)

    private tailrec fun hash(secret: Long, round: Int): Long =
        if (round == 0) {
            secret
        } else {
            var newSecret = ((secret * 64L) mix secret).prune()
            newSecret = ((newSecret / 32f).toLong() mix newSecret).prune()
            newSecret = ((newSecret * 2048L) mix newSecret).prune()
            hash(newSecret, round - 1)
        }

    fun part1(input: List<String>) = input.sumOf { hash(it.toLong(), 2_000) }

    fun part2(input: List<String>): Long {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day22_test", 2024)
    check(Day22.part1(testInput) == 37327623L)
    check(Day22.part2(testInput) == 0L)

    val input = readInput("Day22", 2024)
    println(Day22.part1(input))
    println(Day22.part2(input))
}
