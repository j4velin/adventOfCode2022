package aoc2022

import readInput

private typealias Name = String

object Day21 {
    private class Monkey(
        val name: Name,
        private var number: Long?,
        private val operation: () -> Long? = { number }
    ) {
        companion object {
            lateinit var allMonkeys: Map<Name, Monkey>

            private val pattern =
                """(?<name>[a-z]+): ((?<operand1>[a-z]+) (?<op>[+\-*/]) (?<operand2>[a-z]+))?(?<number>\d+)?""".toPattern()

            fun fromString(input: String): Monkey {
                val matcher = pattern.matcher(input)
                if (!matcher.matches()) {
                    throw IllegalArgumentException("Invalid monkey definition: $input")
                }
                val number = matcher.group("number")?.toLong()
                return if (number != null) {
                    Monkey(matcher.group("name"), number)
                } else {
                    val op = matcher.group("op")

                    val operation = {
                        val n1 = allMonkeys[matcher.group("operand1")]!!.number
                        val n2 = allMonkeys[matcher.group("operand2")]!!.number
                        if (n1 != null && n2 != null) {
                            val result = when (op) {
                                "+" -> n1 + n2
                                "-" -> n1 - n2
                                "*" -> n1 * n2
                                "/" -> n1 / n2
                                else -> throw UnsupportedOperationException("Unsupported operation: $op")
                            }
                            result
                        } else {
                            null
                        }
                    }

                    Monkey(matcher.group("name"), null, operation)
                }
            }
        }

        fun getNumber(): Long? {
            if (number == null) {
                number = operation()
            }
            return number
        }

    }


    fun part1(input: List<String>): Long {
        Monkey.allMonkeys = input.map { Monkey.fromString(it) }.associateBy { it.name }
        val root = Monkey.allMonkeys["root"]!!
        while (root.getNumber() == null) {
            Monkey.allMonkeys.values.forEach { it.getNumber() }
        }
        return root.getNumber()!!
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day21_test", 2022)
    check(Day21.part1(testInput) == 152L)
    check(Day21.part2(testInput) == 0)

    val input = readInput("Day21", 2022)
    println(Day21.part1(input))
    println(Day21.part2(input))
}
