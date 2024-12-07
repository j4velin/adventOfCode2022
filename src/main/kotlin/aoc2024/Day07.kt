package aoc2024

import readInput

object Day07 {

    /**
     * @property result     the expected result
     * @property numbers    the available numbers for this equation
     */
    private class Equation(val result: Long, val numbers: List<Long>) {

        constructor(input: String) : this(
            input.split(":").first().toLong(),
            input.split(": ").last().split("\\s".toRegex()).map { it.toLong() })

        fun canBeCorrect(withConcatOperation: Boolean = false) =
            getPossibleResults(numbers.first(), result, numbers.drop(1), withConcatOperation).any { it == result }

        /**
         * @param resultSoFar           the result accumulated up until this point
         * @param max                   the final expected result. All our operations only increase the outcome,
         *                              so this can be seen as an upper bound
         * @param remainingNumbers      the list of numbers which still needs to be processed
         * @param withConcatOperation   true, if the "concat" operation is allowed
         *
         * @return the list of possible outcomes, when processing the remaining [remainingNumbers]
         */
        private fun getPossibleResults(
            resultSoFar: Long,
            max: Long,
            remainingNumbers: List<Long>,
            withConcatOperation: Boolean,
        ): List<Long> =
            if (remainingNumbers.isEmpty()) {
                listOf(resultSoFar)
            } else {
                val possibleOperations = mutableListOf<Long>()

                if (resultSoFar + remainingNumbers.first() <= max)
                    possibleOperations.add(resultSoFar + remainingNumbers.first())

                if (resultSoFar * remainingNumbers.first() <= max)
                    possibleOperations.add(resultSoFar * remainingNumbers.first())

                val concat = "${resultSoFar}${remainingNumbers.first()}".toLong()
                if (withConcatOperation && concat <= max)
                    possibleOperations.add(concat)

                possibleOperations.flatMap { newResult ->
                    getPossibleResults(
                        newResult,
                        max,
                        remainingNumbers.drop(1),
                        withConcatOperation,
                    )
                }
            }
    }

    fun part1(input: List<String>): Long {
        return input.map { Equation(it) }.filter { it.canBeCorrect() }.sumOf { it.result }
    }

    fun part2(input: List<String>): Long {
        return input.map { Equation(it) }.filter { it.canBeCorrect(withConcatOperation = true) }.sumOf { it.result }
    }
}

fun main() {
    val testInput = readInput("Day07_test", 2024)
    check(Day07.part1(testInput) == 3749L)
    check(Day07.part2(testInput) == 11387L)

    val input = readInput("Day07", 2024)
    println(Day07.part1(input))
    println(Day07.part2(input))
}
