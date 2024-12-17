package aoc2024

import modulo
import readInput
import kotlin.math.floor
import kotlin.math.pow

object Day17 {

    private data class Computer(private var registerA: Long, private var registerB: Long, private var registerC: Long) {

        companion object {
            fun fromString(input: List<String>): Computer {
                val regex = """\D*(\d+)""".toRegex()
                val getValue: (String) -> Long = {
                    regex.find(it)?.groupValues?.get(1)?.toLong()
                        ?: throw IllegalArgumentException("does not match: $it")
                }
                return Computer(getValue(input[0]), getValue(input[1]), getValue(input[2]))
            }
        }

        private var instructionPointer: Int = 0

        private fun getComboOperand(operand: Int): Long = when (operand) {
            in 0..3 -> operand.toLong()
            4 -> registerA
            5 -> registerB
            6 -> registerC
            else -> throw IllegalArgumentException("Invalid combo operand: $operand")
        }

        fun runProgram(program: List<Int>): String {

            val output = mutableListOf<Long>()

            while (instructionPointer < program.size) {
                val instruction = Instruction.fromOpCode(program[instructionPointer])
                val operand = program[instructionPointer + 1]

                when (instruction) {
                    Instruction.ADV, Instruction.BDV, Instruction.CDV -> {
                        val numerator = registerA
                        val denominator = 2.0.pow(getComboOperand(operand).toDouble())
                        val result = (numerator / denominator).toLong()
                        when (instruction) {
                            Instruction.ADV -> registerA = result
                            Instruction.BDV -> registerB = result
                            Instruction.CDV -> registerC = result
                            else -> throw IllegalStateException()
                        }
                    }

                    Instruction.BXL -> registerB = registerB xor operand.toLong()
                    Instruction.BST -> registerB = getComboOperand(operand) % 8
                    Instruction.JNZ -> if (registerA != 0L) {
                        instructionPointer = operand - 2
                    }

                    Instruction.BXC -> registerB = registerB xor registerC
                    Instruction.OUT -> output.add(getComboOperand(operand) % 8)
                }
                instructionPointer += 2
            }

            return output.joinToString(separator = ",")
        }

    }

    private enum class Instruction(val opcode: Int) {

        ADV(0), // division, numerator from A, denominator 2^(combo-operand) -> truncated to INT & stored in A
        BXL(1), // bitwise XOR of B and literal operand -> stored in B
        BST(2), // combo-operand modulo 8 -> B
        JNZ(3), // NOP if A==0, JUMP to literal operator else
        BXC(4), // bitwise XOR of B and C -> B (ignores operand)
        OUT(5), // combo-operand modulo 8 -> OUTPUT
        BDV(6), // like ADV but stored in B
        CDV(7); // like ADV but stored in C

        companion object {
            fun fromOpCode(opcode: Int): Instruction = entries.first { it.opcode == opcode }
        }
    }

    fun part1(input: List<String>): String {
        val computer = Computer.fromString(input.take(3))
        val program = input.last().split(" ").last().split(",").map { it.toInt() }
        return computer.runProgram(program)
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day17_test", 2024)
    check(Day17.part1(testInput) == "4,6,3,5,6,3,5,2,1,0")
    check(Day17.part2(testInput) == 0)

    val input = readInput("Day17", 2024)
    println(Day17.part1(input))
    println(Day17.part2(input))
}
