package aoc2023

import readInput
import java.util.*
import java.util.regex.Pattern
import kotlin.math.pow

private data class Card(val id: Int, val winningNumbers: Set<Int>, val cardNumbers: Set<Int>) {
    val matches = winningNumbers.intersect(cardNumbers)
    val worth = if (matches.isEmpty()) 0 else 2f.pow(matches.size - 1).toInt()

    companion object {
        private val regex = Pattern.compile("""Card\s+(?<id>\d+): (?<winning>[\d\s]+) \| (?<card>[\d\s]+)""")
        fun fromString(input: String): Card {
            val matcher = regex.matcher(input)
            if (matcher.matches()) {
                val winningNumbers =
                    matcher.group("winning").split(' ').filter { it.isNotBlank() }.map { it.toInt() }.toSet()
                val cardNumbers = matcher.group("card").split(' ').filter { it.isNotBlank() }.map { it.toInt() }.toSet()
                return Card(matcher.group("id").toInt(), winningNumbers, cardNumbers)
            } else {
                throw IllegalArgumentException("Does not match: $input")
            }
        }
    }
}


object Day04 {
    fun part1(input: List<String>) = input.map { Card.fromString(it) }.sumOf { it.worth }

    fun part2(input: List<String>): Int {
        val cardMap = input.map { Card.fromString(it) }.associateBy { it.id }
        val myCards = cardMap.values.associateWith { 0 }.toMutableMap()
        cardMap.values.forEach { card ->
            val currentAmount = myCards[card] ?: 1
            val newAmount = currentAmount + 1
            myCards[card] = newAmount
            repeat(card.matches.size) {
                val wonCardId = card.id + it + 1
                val wonCard = cardMap[wonCardId] ?: throw IllegalArgumentException("Unknown card: $wonCardId")
                myCards[wonCard] = (myCards[wonCard] ?: 0) + newAmount
            }
        }
        return myCards.values.sum()
    }
}

fun main() {
    val testInput = readInput("Day04_test", 2023)
    check(Day04.part1(testInput) == 13)
    check(Day04.part2(testInput) == 30)

    val input = readInput("Day04", 2023)
    println(Day04.part1(input))
    println(Day04.part2(input))
}
