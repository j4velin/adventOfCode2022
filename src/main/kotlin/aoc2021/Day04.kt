package aoc2021

import readInput

private const val BOARD_SIZE = 5

/**
 * A bingo board.
 * @see [createBoards]
 */
private class Board private constructor(data: List<String>) {

    // speed up bingo checking by keeping the data twice: organized by rows & by columns
    private val rows: Array<MutableSet<Int>> = (0 until BOARD_SIZE).map { mutableSetOf<Int>() }.toTypedArray()
    private val columns: Array<MutableSet<Int>> = (0 until BOARD_SIZE).map { mutableSetOf<Int>() }.toTypedArray()

    companion object {

        /**
         * Creates some bingo boards
         * @param input the input as specified in https://adventofcode.com/2021/day/4
         * @return a list of bingo boards corresponding to the input data
         */
        fun createBoards(input: List<String>) =
            input.drop(1).filter { it.isNotBlank() }.windowed(BOARD_SIZE, BOARD_SIZE).map { Board(it) }
    }

    init {
        data.map { row -> row.split(" ").filter { it.isNotBlank() }.map { it.toInt() } }.forEachIndexed { index, row ->
            rows[index] = row.toMutableSet()
            for (i in row.withIndex()) {
                columns[i.index].add(i.value)
            }
        }
    }

    /**
     * Marks the given number on the board and checks for a bingo
     * @param number the number to mark
     * @return the score, if a bingo happened on this board or null otherwise
     */
    fun mark(number: Int): Int? {
        var matched = false
        var bingo = false
        rows.forEach {
            if (it.remove(number)) {
                matched = true
                bingo = bingo or it.isEmpty()
            }
        }
        // if a number matched in a row, it must also be present in a column
        if (matched) {
            columns.forEach {
                if (it.remove(number)) {
                    bingo = bingo or it.isEmpty()
                }
            }
        }
        return if (bingo) number * rows.sumOf { it.sum() } else null
    }
}

private fun part1(input: List<String>): Int {
    val inputNumbers = input.first().split(",").map { it.toInt() }
    val boards = Board.createBoards(input)
    inputNumbers.forEach { number ->
        boards.forEach {
            it.mark(number)?.run { return this }
        }
    }
    // no winner
    return 0
}

private fun part2(input: List<String>): Int {
    val inputNumbers = input.first().split(",").map { it.toInt() }
    var boards = Board.createBoards(input)
    inputNumbers.forEach { number ->
        when {
            boards.isEmpty() -> return 0 // no single last winner
            boards.size > 1 -> boards = boards.filter { it.mark(number) == null }
            else -> boards.first().mark(number)?.run { return this }
        }
    }
    // loser board does never win -> can not calculate score
    return 0
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
