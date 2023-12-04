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

            val pattern =
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

    private sealed class Result

    /**
     * x * multiplier + offset
     */
    private data class UnknownNumber(val multiplier: Double = 1.0, val offset: Double = 0.0) : Result() {
        override fun toString() = "${multiplier}x ${if (offset >= 0) "+ " else ""}$offset"
    }

    private data class Number(val value: Double) : Result() {
        override fun toString() = value.toString()
    }

    private data class Operation(
        val operand1: String,
        val operand2: String,
        val operation: String
    ) : Result()

    private class Monkey2(val name: Name, var result: Result) {
        companion object {
            lateinit var allMonkeys: Map<Name, Monkey2>

            fun fromString(input: String): Monkey2 {
                val matcher = Monkey.pattern.matcher(input)
                if (!matcher.matches()) {
                    throw IllegalArgumentException("Invalid monkey definition: $input")
                }
                val name = matcher.group("name")
                if (name == "humn") {
                    return Monkey2(name, UnknownNumber())
                }
                val number = matcher.group("number")?.toDouble()
                return if (number != null) {
                    Monkey2(name, Number(number))
                } else {
                    Monkey2(
                        name, Operation(
                            matcher.group("operand1"),
                            matcher.group("operand2"),
                            matcher.group("op")
                        )
                    )
                }
            }

            private fun executeOperation(op: String, o1: Result, o2: Result): Result? {
                return when {
                    o1 is Number && o2 is Number -> when (op) {
                        "+" -> Number(o1.value + o2.value)
                        "-" -> Number(o1.value - o2.value)
                        "*" -> Number(o1.value * o2.value)
                        "/" -> Number(o1.value / o2.value)
                        else -> throw UnsupportedOperationException("$o1 $op $o2")
                    }
                    o1 is UnknownNumber && o2 is Number -> when (op) {
                        "+" -> o1.copy(offset = o1.offset + o2.value)
                        "-" -> o1.copy(offset = o1.offset - o2.value)
                        "*" -> o1.copy(multiplier = o1.multiplier * o2.value, offset = o1.offset * o2.value)
                        "/" -> o1.copy(multiplier = o1.multiplier / o2.value, offset = o1.offset / o2.value)
                        else -> throw UnsupportedOperationException("$o1 $op $o2")
                    }
                    o1 is Number && o2 is UnknownNumber -> when (op) {
                        "+" -> o2.copy(offset = o2.offset + o1.value)
                        "-" -> o2.copy(
                            multiplier = o2.multiplier * -1,
                            offset = o1.value - o2.offset
                        )
                        "*" -> o2.copy(multiplier = o2.multiplier * o1.value, offset = o2.offset * o1.value)
                        "/" -> o2.copy(
                            multiplier = o1.value / o2.multiplier,
                            offset = o1.value / o2.offset
                        )
                        else -> throw UnsupportedOperationException("$o1 $op $o2")
                    }
                    o1 is UnknownNumber && o2 is UnknownNumber -> throw UnsupportedOperationException("Not implemented: $o1 $op $o2")
                    else -> null
                }
            }
        }

        fun reduce(): Result {
            if (name != "root" && result is Operation) {
                val op = result as Operation
                val o1 = allMonkeys[op.operand1]!!
                val o2 = allMonkeys[op.operand2]!!
                val newResult = executeOperation(op.operation, o1.result, o2.result)
                if (newResult != null) {
                    result = newResult
                }
            }
            return result
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

    fun part2(input: List<String>): String {
        Monkey2.allMonkeys = input.map { Monkey2.fromString(it) }.associateBy { it.name }
        val rootOp = Monkey2.allMonkeys["root"]!!.result as Operation
        var canBeReduces = true
        while (canBeReduces) {
            canBeReduces = Monkey2.allMonkeys.values.any { it.result != it.reduce() }
        }
        return "${Monkey2.allMonkeys[rootOp.operand1]!!.result} = ${Monkey2.allMonkeys[rootOp.operand2]!!.result}"
    }
}

fun main() {
    val testInput = readInput("Day21_test", 2022)
    check(Day21.part1(testInput) == 152L)
    check(Day21.part2(testInput) == "0.5x -0.5 = 150.0")

    val input = readInput("Day21", 2022)
    println(Day21.part1(input))
    println(Day21.part2(input)) // use wolframalpha.com to solve...
}
