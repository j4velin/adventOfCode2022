package aoc2024

import readInput
import separateBy

object Day24 {

    private data class Wire(val name: String, var value: Boolean? = null)

    private data class LogicOperation(val operation: String, val input1: Wire, val input2: Wire, val output: Wire)

    fun part1(input: List<String>): Long {

        val (wireInput, logicInput) = input.separateBy { it.isEmpty() }

        val wires = wireInput.associate {
            val split = it.split(": ")
            split.first() to Wire(split.first(), split.last() == "1")
        }.toMutableMap()

        val regex = """(?<i1>.+) (?<op>AND|OR|XOR) (?<i2>.+) -> (?<out>.*)""".toRegex()
        val logics = logicInput.map {
            val match = regex.matchEntire(it) ?: throw IllegalArgumentException("does not match: $it")
            val i1 = wires[match.groups["i1"]!!.value]
                ?: Wire(match.groups["i1"]!!.value).also { w -> wires[w.name] = w }
            val i2 = wires[match.groups["i2"]!!.value]
                ?: Wire(match.groups["i2"]!!.value).also { w -> wires[w.name] = w }
            val out = wires[match.groups["out"]!!.value]
                ?: Wire(match.groups["out"]!!.value).also { w -> wires[w.name] = w }
            LogicOperation(match.groups["op"]!!.value, i1, i2, out)
        }

        val finalOutputWires = wires.filter { it.key.startsWith("z") }

        while (finalOutputWires.any { it.value.value == null }) {
            logics.filter { it.input1.value != null && it.input2.value != null }.forEach { logic ->
                when (logic.operation) {
                    "AND" -> logic.output.value = logic.input1.value!! && logic.input2.value!!
                    "OR" -> logic.output.value = logic.input1.value!! || logic.input2.value!!
                    "XOR" -> logic.output.value = logic.input1.value!! xor logic.input2.value!!
                }
            }
        }

        val binary = finalOutputWires.values.sortedByDescending { it.name }
            .joinToString(separator = "") { if (it.value == true) "1" else "0" }

        return binary.toLong(2)
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day24_test", 2024)
    check(Day24.part1(testInput) == 2024L)
    check(Day24.part2(testInput) == 0)

    val input = readInput("Day24", 2024)
    println(Day24.part1(input))
    println(Day24.part2(input))
}
