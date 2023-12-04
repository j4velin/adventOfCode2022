package aoc2022

import popTo
import readInput
import java.util.Stack

data class Instruction(val count: Int, val from: Int, val to: Int) {
    companion object {
        fun fromString(str: String): Instruction {
            // example: move 1 from 2 to 1
            val split = str.split(" ").toList().mapNotNull { it.toIntOrNull() }
            return Instruction(split[0], split[1] - 1, split[2] - 1)
        }
    }
}

fun main() {

    fun parseInitialStackConfig(input: List<String>): List<Stack<Char>> {
        val stackInfo = input.withIndex().dropWhile { !it.value.startsWith(" 1") }.first()
        val stacks = stackInfo.value.split("   ").map { Stack<Char>() }
        var currentLine = stackInfo.index - 1
        while (currentLine >= 0) {
            input[currentLine].withIndex().filter { it.value.isLetter() }.forEach {
                stacks[it.index / 4].add(it.value)
            }
            currentLine--
        }
        return stacks
    }

    fun generateOutput(stacks: List<Stack<Char>>) =
        stacks.map { it.peek() }.joinToString(separator = "") { it.toString() }

    fun parseInstructions(input: List<String>): List<Instruction> =
        input.dropWhile { it.isNotBlank() }.filter { it.isNotBlank() }.map { Instruction.fromString(it) }

    fun part1(input: List<String>): String {
        val stacks = parseInitialStackConfig(input)
        val instructions = parseInstructions(input)

        instructions.forEach { stacks[it.from].popTo(stacks[it.to], it.count) }

        return generateOutput(stacks)
    }

    fun part2(input: List<String>): String {
        val stacks = parseInitialStackConfig(input)
        val instructions = parseInstructions(input)

        instructions.forEach {
            val tmpStack = Stack<Char>()
            stacks[it.from].popTo(tmpStack, it.count)
            tmpStack.popTo(stacks[it.to], it.count)
        }

        return generateOutput(stacks)
    }

    val testInput = readInput("Day05_test", 2022)
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05", 2022)
    println(part1(input))
    println(part2(input))
}
