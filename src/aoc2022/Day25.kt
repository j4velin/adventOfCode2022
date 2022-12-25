package aoc2022

import readInput

typealias SNAFU = String

object Day25 {

    private fun Char.digitFromSNAFU(): Int {
        return when (this) {
            '=' -> -2
            '-' -> -1
            else -> this.digitToInt()
        }
    }

    private fun SNAFU.toDecimal(): Long {
        val array = this.toCharArray()
        var result = 0L
        var multiplier = 1
        for (i in array.size - 1 downTo 0) {
            val decimalDigit = array[i].digitFromSNAFU()
            result += decimalDigit * multiplier
            multiplier *= 5
        }
        return result
    }

    private fun Long.toSNAFU(): String {
        val str = this.toString(5).toCharArray()
        var buffer = ""
        var carry = 0
        for (i in str.size - 1 downTo 0) {
            val digit = str[i].digitToInt() + carry
            buffer += when (digit) {
                3 -> '='
                4 -> '-'
                else -> digit
            }
            carry = if (digit >= 3) 1 else 0
        }
        if (carry > 0) {
            buffer += carry
        }
        return buffer.reversed()
    }

    fun part1(input: List<String>): String {
        val decimalSum = input.sumOf { it.toDecimal() }
        val snafu = decimalSum.toSNAFU()
        println("decimal: $decimalSum -> $snafu")
        return snafu
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day25_test", 2022)
    check(Day25.part1(testInput) == "2=-1=0")
    check(Day25.part2(testInput) == 0)

    val input = readInput("Day25", 2022)
    println(Day25.part1(input))
    println(Day25.part2(input))
}
