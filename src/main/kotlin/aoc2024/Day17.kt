package aoc2024

import readInput
import kotlin.math.pow

object Day17 {

    private data class Computer(
        private var registerA: Long,
        private var registerB: Long = 0L,
        private var registerC: Long = 0L
    ) {

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

        fun runProgram(program: List<Int>, expectedOutput: List<Long>? = null): List<Long> {

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
                    Instruction.OUT -> {
                        output.add(getComboOperand(operand) % 8)
                        // early return for pt 2
                        if (expectedOutput != null && expectedOutput.take(output.size) != output) {
                            return emptyList()
                        }
                    }
                }
                instructionPointer += 2
            }
            return output
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
        return computer.runProgram(program).joinToString(separator = ",")
    }

    fun part2(input: List<String>): Long {
        // handcrafted for my specific input:
        // (1) 2,4 -> b = a % 8
        // (2) 1,1 -> b = b xor 1
        // (3) 7,5 -> c = a / 2^b
        // (4) 0,3 -> a = a / 2^3
        // (5) 4,3 -> b = b xor c
        // (6) 1,6 -> b = b xor 6
        // (7) 5,5 -> output: b % 8
        // (8) 3,0 -> a != 0 ? jmp to 0 again

        // we output the last 3 bits of 'b' in each iteration
        // a is divided by 8 in each iteration
        // 1 output per iteration -> 16 iterations
        // a must be 0 at the end of the 16th iteration
        // last output must be "0"

        val program = input.last().split(" ").last().split(",").map { it.toInt() }
        val simulate: (Long, List<Long>) -> Long = { expectedA, expectedOutput ->
            // (4) a = a / 2^3
            // --> a / 8 >= expectedA
            // --> a >= expectedA * 8
            var a = expectedA * 8 - 1   // -1 because we start our loop with a++
            var result = listOf<Long>()
            while (result != expectedOutput) {
                a++
                val computer = Computer(registerA = a)
                result = computer.runProgram(program, expectedOutput)
            }
            a
        }

        val expectedOutput = mutableListOf<Long>()
        var lastResult = 0L
        program.reversed().forEach { digit ->
            expectedOutput.addFirst(digit.toLong())
            lastResult = simulate(lastResult, expectedOutput)
        }

        return lastResult
    }
}

fun main() {
    val testInput = readInput("Day17_test", 2024)
    check(Day17.part1(testInput) == "4,6,3,5,6,3,5,2,1,0")

    val input = readInput("Day17", 2024)
    println(Day17.part1(input))
    println(Day17.part2(input))
}
