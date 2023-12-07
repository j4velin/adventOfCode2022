package aoc2023

import readInput

private val cardOrder = charArrayOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A').reversed()
private val cardOrderWithJokerRule =
    charArrayOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A').reversed()

private enum class Type {
    FiveOfAKind,
    FourOfAKind,
    FullHouse,
    ThreeOfAKind,
    TwoPairs,
    OnePairs,
    HighCard;

    companion object {
        fun getType(cards: List<Char>): Type {
            val occurences = cards.groupBy { it }
            return when (occurences.size) {
                1 -> FiveOfAKind
                2 -> {
                    // FourOfAKind or FullHouse
                    if (occurences.values.maxOf { it.size } == 4) {
                        FourOfAKind
                    } else {
                        FullHouse
                    }
                }

                3 -> {
                    // ThreeOfAKind or TwoPairs
                    if (occurences.values.maxOf { it.size } == 3) {
                        ThreeOfAKind
                    } else {
                        TwoPairs
                    }
                }

                4 -> OnePairs
                5 -> HighCard
                else -> throw IllegalArgumentException("something wrong this these cards: $cards (${cards.size})")
            }
        }

        fun getTypeWithJokerRule(cards: List<Char>): Type {
            val occurencesWithOutJoker = cards.filter { it != 'J' }.groupBy { it }
            val jokers = cards.count { it == 'J' }
            if (jokers == 0) return getType(cards)
            return when (jokers) {
                5, 4 -> FiveOfAKind
                3 -> { // 2 other cards
                    if (occurencesWithOutJoker.size == 1) {
                        // remaining is a pair
                        FiveOfAKind
                    } else {
                        FourOfAKind
                    }
                }

                2 -> { // 3 other cards
                    when (occurencesWithOutJoker.size) {
                        1 -> FiveOfAKind // all other cards are the same
                        2 -> FourOfAKind // one pair
                        3 -> ThreeOfAKind // all different
                        else -> throw IllegalArgumentException("something wrong this these cards: $cards (${cards.size})")
                    }
                }

                1 -> { // 4 other cards
                    when (occurencesWithOutJoker.size) {
                        1 -> FiveOfAKind // all other cards are the same
                        2 -> {
                            // two pairs or 3-1
                            if (occurencesWithOutJoker.values.first().size == 2) {
                                FullHouse
                            } else {
                                FourOfAKind
                            }
                        }

                        3 -> ThreeOfAKind // one pair
                        4 -> OnePairs // all different
                        else -> throw IllegalArgumentException("something wrong this these cards: $cards (${cards.size})")
                    }
                }

                else -> throw IllegalArgumentException("something wrong this these cards: $cards (${cards.size})")
            }
        }
    }
}

private data class Hand(val cards: List<Char>, val bid: Int, val withJokerRule: Boolean) : Comparable<Hand> {
    companion object {
        fun fromString(input: String, jokerRule: Boolean): Hand {
            val split = input.split(" ")
            return Hand(split.first().toList(), split.last().toInt(), jokerRule)
        }
    }

    val type = if (withJokerRule) Type.getTypeWithJokerRule(cards) else Type.getType(cards)

    override fun compareTo(other: Hand): Int {
        if (type != other.type) {
            return type.compareTo(other.type)
        } else {
            for (i in cards.indices) {
                if (cards[i] != other.cards[i]) {
                    return if (withJokerRule) {
                        cardOrderWithJokerRule.indexOf(cards[i])
                            .compareTo(cardOrderWithJokerRule.indexOf(other.cards[i]))
                    } else {
                        cardOrder.indexOf(cards[i]).compareTo(cardOrder.indexOf(other.cards[i]))
                    }
                }
            }
            return 0
        }
    }

}

object Day07 {
    fun part1(input: List<String>): Int {
        return input.map { Hand.fromString(it, false) }.sorted().reversed().withIndex()
            .sumOf { (rank, hand) -> (rank + 1) * hand.bid }
    }

    fun part2(input: List<String>): Int {
        return input.map { Hand.fromString(it, true) }.sorted().reversed().withIndex()
            .sumOf { (rank, hand) -> (rank + 1) * hand.bid }
    }
}

fun main() {
    val testInput = readInput("Day07_test", 2023)
    check(Day07.part1(testInput) == 6440)
    check(Day07.part2(testInput) == 5905)

    val input = readInput("Day07", 2023)
    println(Day07.part1(input))
    println(Day07.part2(input))
}
