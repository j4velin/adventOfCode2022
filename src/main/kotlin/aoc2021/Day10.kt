package aoc2021

import readInput
import java.util.*

/**
 * A character in a chunk.
 * @property open the open symbol
 * @property close the close symbol
 * @property corruptScore the score this character gives, if it is the one corrupting the chunk
 * @property missingScore the amount this character adds to the score, if it is missing in a chunk
 */
private enum class ChunkChar(val open: Char, val close: Char, val corruptScore: Int, val missingScore: Int) {
    A('(', ')', 3, 1), B('[', ']', 57, 2), C('{', '}', 1197, 3), D('<', '>', 25137, 4);

    companion object {
        /**
         * Valid opening symbols
         */
        val openings = values().map { it.open }

        /**
         * Valid closing symbols
         */
        val closings = values().map { it.close }

        /**
         * @param c the open or close symbol of a chunk character
         * @return the corresponding chunk character
         */
        fun from(c: Char) = values().first { it.open == c || it.close == c }
    }

    override fun toString() = open.toString()
}

/**
 * Checks if this chunk is corrupted or incomplete
 */
private fun String.checkChunk(): Result<*, *> {
    val stack = Stack<ChunkChar>()
    for (char in this) {
        when (char) {
            in ChunkChar.openings -> stack.push(ChunkChar.from(char))
            in ChunkChar.closings -> if (stack.pop().close != char) return CorruptResult(ChunkChar.from(char))
        }
    }
    return if (stack.isNotEmpty()) {
        IncompleteResult(stack.reversed())
    } else {
        ValidResult
    }
}

/**
 * Object describing the result of a chunk analysis, see [String.checkChunk]
 */
private sealed class Result<T, N>(val result: T) {
    /**
     * @return the result score
     */
    abstract fun getScore(): N
}

/**
 * Class representing a corrupt chunk result.
 * @property result the invalid character
 */
private class CorruptResult(invalidChar: ChunkChar) : Result<ChunkChar, Int>(invalidChar) {
    override fun getScore() = result.corruptScore
}

/**
 * Class representing an incomplete chunk result.
 * @property result the missing characters
 */
private class IncompleteResult(missing: List<ChunkChar>) : Result<List<ChunkChar>, Long>(missing) {
    override fun getScore() = result.fold(0L) { acc, current -> acc * 5 + current.missingScore }
}

/**
 * Object representing a valid chunk result.
 */
private object ValidResult : Result<Any?, Int>(null) {
    override fun getScore() = 0
}

private fun part1(input: List<String>) =
    input.map { it.checkChunk() }.filterIsInstance(CorruptResult::class.java).sumOf { it.getScore() }

private fun part2(input: List<String>): Long {
    val scores = input.map { it.checkChunk() }.filterIsInstance(IncompleteResult::class.java)
        .map { it.getScore() }.sorted()
    return scores[scores.size / 2]
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
