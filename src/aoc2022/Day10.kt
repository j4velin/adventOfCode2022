package aoc2022

import readInput

fun main() {

    fun part1(input: List<String>): Int {
        var register = 1
        var cycle = 1
        var signalStrenght = 0
        val calculateSignalStrengh = {
            if ((cycle - 20) % 40 == 0) {
                signalStrenght += register * cycle
            }
        }
        input.forEach {
            cycle++
            calculateSignalStrengh()
            if (it != "noop") {
                val delta = it.split(" ").last().toInt()
                register += delta
                cycle++
                calculateSignalStrengh()
            }
        }
        return signalStrenght
    }

    fun part2(input: List<String>) {
        var spritePosition = 0..2
        var cycle = 1
        var register = 1
        val lines = Array((input.size * 2) / 40) { Array(40) { '.' } }
        val drawPixel = {
            val row = cycle / 40
            val pixelPosition = (cycle - 1) % 40
            if (pixelPosition in spritePosition) {
                lines[row][pixelPosition] = '#'
            }
        }
        input.forEach {
            drawPixel()
            cycle++
            drawPixel()
            if (it != "noop") {
                val delta = it.split(" ").last().toInt()
                register += delta
                cycle++
                spritePosition = register - 1..register + 1
            }
        }
        for (line in lines) {
            println(line.joinToString("") { it.toString() })
        }
    }

    val testInput = readInput("Day10_test", 2022)
    check(part1(testInput) == 13140)
    part2(testInput)

    val input = readInput("Day10", 2022)
    println(part1(input))
    part2(input) // FZBPBFZF
}
