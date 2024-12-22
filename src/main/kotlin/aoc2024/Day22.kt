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

        val totalPricePerSequence = mutableMapOf<List<Int>, Long>()
        input.map { initialSecret ->
            val secrets = buildList {
                var current = initialSecret.toLong()
                repeat(2_000) {
                    add(current)
                    val next = hash(current, 1)
                    current = next
                }
            }
            val prices = secrets.map { it modulo 10 }.map { it.toInt() }
            val diffsWithPrices =
                prices.windowed(size = 2, step = 1).withIndex()
                    .map { (idx, value) -> value.last() - value.first() to prices[idx + 1] }

            val sequencesWithPrices = diffsWithPrices.windowed(size = 4, step = 1)
                .map { list -> listOf(list[0].first, list[1].first, list[2].first, list[3].first) to list[3].second }
                .distinctBy { it.first }

            sequencesWithPrices.forEach { (sequence, price) ->
                totalPricePerSequence[sequence] = (totalPricePerSequence[sequence] ?: 0) + price
            }
        }

        return totalPricePerSequence.values.max()

    }
}

fun main() {
    val testInput = readInput("Day22_test", 2024)
    check(Day22.part1(testInput) == 37327623L)
    val testInput2 = readInput("Day22_test2", 2024)
    check(Day22.part2(testInput2) == 23L)

    val input = readInput("Day22", 2024)
    println(Day22.part1(input))
    println(Day22.part2(input))
}
