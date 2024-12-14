package aoc2024

import PointL
import readInput
import separateBy

object Day13 {

    private data class Machine(val buttonA: Button, val buttonB: Button, val prize: PointL) {
        companion object {
            fun fromString(input: List<String>): Machine {
                val buttonA = Button.fromString(input[0])
                val buttonB = Button.fromString(input[1])
                val prizePosition = input[2].split(": ").last().split(", ").map { it.substring(2).toInt() }

                return Machine(buttonA, buttonB, PointL(prizePosition[0], prizePosition[1]))
            }
        }

        fun priceToReachPrize(offset: Long = 0L): Long? {
            /**
             * a * ax + b * bx = px    --> a = (px - (b * bx)) / ax
             * a * ay + b * by = py    --> a = (py - (b * by)) / ay
             *
             *                         --> (px - b * bx) / ax = (py - b * by) / ay
             *                         --> (px - b * bx) * ay = (py - b * by) * ax
             *                         --> (px * ay) - (b * bx * ay) = (py * ax) - (b * by * ax)
             *                         --> (px * ay) - (py * ax) = (b * bx * ay) - (b * by * ax)
             *                         --> (px * ay) - (py * ax) = b * (bx * ay - by * ax)
             *                         --> b = ((px * ay) - (py * ax)) / (bx * ay - by * ax)
             *
             *
             *                         --> (px - a * ax) / bx = (py - a * ay) / by
             *                         --> (px - a * ax) * by = (py - a * ay) * bx
             *                         --> by * px - by * a * ax = bx * py - bx * a * ay
             *                         --> bx * a * ay - by * a * ax = bx * py - by * px
             *                         --> a * (bx * ay - by * ax) = bx * py - by * px
             *                         --> a = (bx * py - by * px) / (bx * ay - by * ax)
             */
            val ax = buttonA.delta.x
            val bx = buttonB.delta.x
            val px = prize.x + offset
            val ay = buttonA.delta.y
            val by = buttonB.delta.y
            val py = prize.y + offset

            val b = ((px * ay) - (py * ax)) / (bx * ay - by * ax)

            /**
             * a * ax + b * bx = px
             * --> a * ax = px - b * bx
             * --> a = (px - b * bx) / ax
             */
            val a = (px - b.toLong() * bx) / ax

            return if (buttonA.delta * a + buttonB.delta * b == PointL(px, py)) {
                a * 3L + b.toLong()
            } else {
                null
            }
        }
    }

    private data class Button(val cost: Int, val delta: PointL) {
        companion object {
            fun fromString(s: String): Button {
                val split = s.split(": ")
                val cost = if (split.first().last() == 'A') 3 else 1
                val deltas = split.last().split(", ").map { it.substring(1).toInt() }
                val delta = PointL(deltas[0], deltas[1])
                return Button(cost, delta)
            }
        }
    }

    fun part1(input: List<String>): Long {
        val machines = input.separateBy { it.isEmpty() }.map { Machine.fromString(it) }
        return machines.mapNotNull { machine -> machine.priceToReachPrize() }.sum().toLong()
    }

    fun part2(input: List<String>): Long {
        val machines = input.separateBy { it.isEmpty() }.map { Machine.fromString(it) }
        return machines.mapNotNull { machine -> machine.priceToReachPrize(10000000000000L) }.sum()
    }
}

fun main() {
    val testInput = readInput("Day13_test", 2024)
    check(Day13.part1(testInput) == 480L)
    //check(Day13.part2(testInput) == 0L)

    val input = readInput("Day13", 2024)
    println(Day13.part1(input))
    println(Day13.part2(input))
}
