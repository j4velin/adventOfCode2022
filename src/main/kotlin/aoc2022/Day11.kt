package aoc2022

import readInput
import separateBy
import kotlin.math.floor

/**
 * @property items              the items currently held by this [Monkey]
 * @property operation          a transformation operation representing the change in the "worry-level"
 * @property testDivisor        the operand for the test operation to decide to which other monkey an item will be thrown
 * @property targetMonkeys      a pair of [Monkey] indices - the first is the index of the monkey, to which this monkey will
 *                              throw an item, if the test hold - otherwise it is thrown to the [Monkey] at the second index
 * @property inspectionCount    the amount of items this [Monkey] has inspected an item so far
 */
data class Monkey(
    val items: MutableList<Long>,
    val operation: (Long) -> Long,
    val testDivisor: Int,
    val targetMonkeys: Pair<Int, Int>
) {
    var inspectionCount = 0
        private set

    companion object {
        fun fromStrings(input: List<String>): Monkey {
            val items = input[0].drop("Starting items: ".length).split(", ").map { it.toLong() }.toMutableList()
            val (op, operand) = input[1].drop("Operation: new = old ".length).split(" ", limit = 2)
            val operation: (Long) -> Long = when (op) {
                "*" -> { old -> old * if (operand == "old") old else operand.toLong() }
                "+" -> { old -> old + if (operand == "old") old else operand.toLong() }
                "-" -> { old -> old - if (operand == "old") old else operand.toLong() }
                "/" -> { old -> old / if (operand == "old") old else operand.toLong() }
                else -> throw UnsupportedOperationException("Unknown operation: ${input[1]}")
            }
            val divisor = input[2].drop("Test: divisible by ".length).toInt()
            val targetIndexTrue = input[3].drop("If true: throw to monkey ".length).toInt()
            val targetIndexFalse = input[4].drop("If false: throw to monkey ".length).toInt()
            return Monkey(items, operation, divisor, targetIndexTrue to targetIndexFalse)
        }
    }

    /**
     * Let this monkey take its turn.
     *
     * Applies [operation] and [reductionMethod] on each item and throws it to the corresponding other monkey.
     *
     * @param monkeys all the monkeys in the correct order
     * @param reductionMethod a method of keeping the worrying levels under control...
     */
    fun takeTurn(monkeys: Array<Monkey>, reductionMethod: (Long) -> Long) {
        if (items.isNotEmpty()) {
            items.map {
                reductionMethod(operation(it))
            }.forEach {
                inspectionCount++
                if (it % testDivisor == 0L) {
                    monkeys[targetMonkeys.first].items.add(it)
                } else {
                    monkeys[targetMonkeys.second].items.add(it)
                }
            }
            items.clear() // we threw all our items to some other monkeys
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val monkeys = input.filter { !it.startsWith("Monkey ") }.map { it.trim() }.separateBy { it.isBlank() }
            .map { Monkey.fromStrings(it) }.toTypedArray()
        repeat(20) {
            monkeys.forEach { it.takeTurn(monkeys) { n -> floor(n / 3f).toLong() } }
        }
        return monkeys.map { it.inspectionCount }.sortedDescending().take(2).reduce { a, b -> a * b }
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.filter { !it.startsWith("Monkey ") }.map { it.trim() }.separateBy { it.isBlank() }
            .map { Monkey.fromStrings(it) }.toTypedArray()
        val commonDivisor = monkeys.map { it.testDivisor }.reduce { a, b -> a * b }.toLong()
        repeat(10000) {
            monkeys.forEach { it.takeTurn(monkeys) { n -> n % commonDivisor } }
        }
        return monkeys.map { it.inspectionCount.toLong() }.sortedDescending().take(2).reduce { a, b -> a * b }
    }

    val testInput = readInput("Day11_test", 2022)
    check(part1(testInput) == 10605)
    check(part2(testInput) == 2713310158)

    val input = readInput("Day11", 2022)
    println(part1(input))
    println(part2(input))
}
