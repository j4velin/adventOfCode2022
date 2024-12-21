package aoc2024

import PointL
import bfs
import readInput

object Day21 {

    sealed class KeyPad(private val keys: Map<Char, PointL>) {

        private var currentPosition: PointL = keys['A'] ?: throw IllegalArgumentException("Button 'A' not found")

        fun enterCode(code: String): String = buildString {
            code.map { target ->
                val start = currentPosition
                val end = keys[target] ?: throw IllegalArgumentException("Button '$target' not found")

                val path = bfs(
                    start = start,
                    validGrid = PointL(0, 0) to PointL(2, 3),
                    neighbourCondition = { _, next -> keys.values.contains(next) },
                    targetCondition = { it == end },
                ).minBy { path ->
                    var turns = 0
                    var previousDelta = PointL(0, 0)
                    path.windowed(size = 2, step = 1).map {
                        val p1 = it.first()
                        val p2 = it.last()
                        val delta = p2 - p1
                        if (delta != previousDelta) turns++
                        previousDelta = delta
                    }
                    turns
                }

                path.forEach { next ->
                    val dx = (next.x - currentPosition.x).toInt()
                    val dy = (next.y - currentPosition.y).toInt()

                    if (dx > 0) append('>')
                    else if (dx < 0) append('<')

                    if (dy > 0) append('v')
                    else if (dy < 0) append('^')

                    currentPosition = next
                }

                append('A')
            }
        }
    }

    private class NumericKeypad : KeyPad(
        mapOf(
            '7' to PointL(0, 0),
            '8' to PointL(1, 0),
            '9' to PointL(2, 0),
            '4' to PointL(0, 1),
            '5' to PointL(1, 1),
            '6' to PointL(2, 1),
            '1' to PointL(0, 2),
            '2' to PointL(1, 2),
            '3' to PointL(2, 2),
            '0' to PointL(1, 3),
            'A' to PointL(2, 3),
        )
    )

    private class DirectionalKeypad : KeyPad(
        mapOf(
            '^' to PointL(1, 0),
            'A' to PointL(2, 0),
            '<' to PointL(0, 1),
            'v' to PointL(1, 1),
            '>' to PointL(2, 1),
        )
    )

    fun part1(input: List<String>): Int {

        val robot1 = DirectionalKeypad()
        val robot2 = DirectionalKeypad()
        val robot3 = NumericKeypad()

        return input.sumOf {
            val buttonPresses = robot1.enterCode(robot2.enterCode(robot3.enterCode(it)))
            val result = buttonPresses.length * it.replace("""\D*""".toRegex(), "").toInt()
            result
        }
    }

    fun part2(input: List<String>): Int {
        return 0
        /*
                val doorRobot = NumericKeypad()
                val directionalKeypads = (1..25).map { DirectionalKeypad() }

                return input.sumOf { code ->

                    var codeToEnter = doorRobot.enterCode(code)

                    directionalKeypads.forEach {
                        codeToEnter = it.enterCode(codeToEnter)
                    }

                    val buttonPresses = codeToEnter

                    val result = buttonPresses.length * code.replace("""\D*""".toRegex(), "").toInt()
                    result
                }

         */
    }
}

fun main() {
    val testInput = readInput("Day21_test", 2024)
    check(Day21.part1(testInput) == 126384)
    check(Day21.part2(testInput) == 0)

    val input = readInput("Day21", 2024)
    println(Day21.part1(input))
    println(Day21.part2(input))
}
