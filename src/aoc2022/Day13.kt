package aoc2022

import readInput
import separateBy
import java.util.*

private object PacketComparator : Comparator<PacketData> {
    override fun compare(left: PacketData, right: PacketData): Int {
        //println("compare $left vs $right")
        if (left is IntData) {
            return if (right is IntData) {
                left.value.compareTo(right.value)
            } else {
                compare(ListData(listOf(left)), right)
            }
        } else if (right is IntData) {
            return compare(left, ListData(listOf(right)))
        }
        // both are lists
        if (left is ListData && right is ListData) {
            for (i in left.value.indices) {
                val l = left.value[i]
                if (right.value.size >= i + 1) {
                    val r = right.value[i]
                    val compare = compare(l, r)
                    if (compare != 0) {
                        return compare
                    }
                } else {
                    return 1
                }
            }
            return if (right.value.size > left.value.size) {
                -1
            } else {
                0
            }
        } else {
            throw IllegalArgumentException("Unknown type: $left, $right")
        }
    }
}

sealed class PacketData

data class IntData(val value: Int) : PacketData() {
    override fun toString(): String {
        return value.toString()
    }
}

data class ListData(val value: List<PacketData>) : PacketData() {
    companion object {
        fun fromString(input: String): ListData {
            val lists = Stack<MutableList<PacketData>>()
            var currentInt = ""
            input.forEach {
                if (it.isDigit()) {
                    currentInt += it
                } else if (currentInt.isNotEmpty()) {
                    lists.peek().add(IntData(currentInt.toInt()))
                    currentInt = ""
                }
                when (it) {
                    '[' -> lists.add(mutableListOf())
                    ']' -> {
                        val l = lists.pop()
                        if (lists.isEmpty()) {
                            return ListData(l)
                        } else {
                            lists.peek().add(ListData(l))
                        }
                    }

                    else -> {} // ignore
                }
            }
            throw IllegalArgumentException("Invalid input: $input")
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}

private fun part1(input: List<String>): Int {
    return input.separateBy { it.isBlank() }.map { packetPair ->
        val leftPacket = ListData.fromString(packetPair[0])
        val rightPacket = ListData.fromString(packetPair[1])
        PacketComparator.compare(leftPacket, rightPacket)
    }.withIndex().filter { it.value == -1 }.sumOf { it.index + 1 }
}

private fun part2(input: List<String>): Int {
    val list = input.filter { it.isNotBlank() }.map { ListData.fromString(it) }.toMutableList()
    val dividerPacket1 = ListData(listOf(IntData(2)))
    val dividerPacket2 = ListData(listOf(IntData(6)))
    list.add(dividerPacket1)
    list.add(dividerPacket2)
    val sorted = list.sortedWith(PacketComparator)
    return (sorted.indexOf(dividerPacket1) + 1) * (sorted.indexOf(dividerPacket2) + 1)
}

fun main() {
    val testInput = readInput("Day13_test", 2022)
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13", 2022)
    println(part1(input))
    println(part2(input))
}
