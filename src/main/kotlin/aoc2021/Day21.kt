package aoc2021

import modulo
import readInput
import withEachOf
import kotlin.math.max

private data class Player(var position: Int, var score: Int = 0) {

    /**
     * Moves the player on the game board
     *
     * @param distance the distance by which to advance the player
     * @return the player itself to chain commands
     */
    fun move(distance: Int): Player {
        position = (position + distance.modulo(10)).modulo(10)
        score += position
        return this
    }
}

private data class DeterministicDice(private var rollCount: Int = 0) {

    private var diceValue = 0
    val rolls: Int
        get() = rollCount

    private fun throwDice(): Int {
        rollCount++
        diceValue = (diceValue + 1).modulo(100)
        return diceValue
    }

    fun throwDice(times: Int): Int {
        var sum = 0
        repeat(times) {
            sum += throwDice()
        }
        return sum
    }

}

private fun part1(input: List<String>): Int {
    val dice = DeterministicDice()
    val player1 = Player(input[0].substring("aoc2021.Player 1 starting position: ".length).toInt())
    val player2 = Player(input[1].substring("aoc2021.Player 2 starting position: ".length).toInt())
    while (true) {
        var diceSum = dice.throwDice(3)
        player1.move(diceSum)
        if (player1.score >= 1000)
            return dice.rolls * player2.score

        diceSum = dice.throwDice(3)
        player2.move(diceSum)
        if (player2.score >= 1000)
            return dice.rolls * player1.score
    }
}

private class DiracGame {
    var player1WinCount = 0L
        private set
    var player2WinCount = 0L
        private set

    /**
     * A map of possible results and their frequency when throwing a 3-sided dirac dice 3 times
     */
    private val diracDiceThrows = sequenceOf(1, 2, 3).withEachOf(sequenceOf(1, 2, 3)).withEachOf(sequenceOf(1, 2, 3))
        .map { it.first.first + it.first.second + it.second }.groupBy { it }.mapValues { it.value.size }

    fun play(player1: Player, player2: Player, multiplier: Long = 1L) {
        diracDiceThrows.forEach { (distance, frequency) ->
            val p1 = player1.copy().move(distance)
            if (p1.score >= 21) {
                player1WinCount += (multiplier * frequency)
            } else {
                diracDiceThrows.forEach { (distance2, frequency2) ->
                    val p2 = player2.copy().move(distance2)
                    if (p2.score >= 21) {
                        player2WinCount += (multiplier * frequency2)
                    } else {
                        play(p1, p2, multiplier * frequency * frequency2)
                    }
                }
            }
        }
    }

}

private fun part2(input: List<String>): Long {
    val player1 = Player(input[0].substring("aoc2021.Player 1 starting position: ".length).toInt())
    val player2 = Player(input[1].substring("aoc2021.Player 2 starting position: ".length).toInt())
    val game = DiracGame()
    game.play(player1, player2)
    return max(game.player1WinCount, game.player2WinCount)
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 739785)
    check(part2(testInput) == 444356092776315L)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
