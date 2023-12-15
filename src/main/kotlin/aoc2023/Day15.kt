package aoc2023

import readInput


private data class Lense(val label: String, var focalLength: Int)
private data class Box(val lenses: MutableList<Lense> = mutableListOf())

object Day15 {

    private fun hash(input: String): Int {
        var currentValue = 0
        input.forEach { char ->
            currentValue += char.code
            currentValue *= 17
            currentValue %= 256
        }
        return currentValue
    }

    fun part1(input: List<String>) = input.first().split(",").sumOf { hash(it) }


    fun part2(input: List<String>): Int {
        val boxes = Array(256) { Box() }
        val regex = """(?<label>.*)(?<operation>[-=])(?<focalLength>\d?)""".toRegex()
        input.first().split(",").forEach { str ->
            val matchResult = regex.find(str) ?: throw IllegalArgumentException("Did not match: $str")
            val label = matchResult.groups["label"]!!.value
            val box = boxes[hash(label)]
            when (val operation = matchResult.groups["operation"]!!.value) {
                "-" -> box.lenses.removeIf { it.label == label }
                "=" -> {
                    val focalLength = matchResult.groups["focalLength"]!!.value.toInt()
                    val existingLense = box.lenses.firstOrNull { it.label == label }
                    if (existingLense != null) {
                        existingLense.focalLength = focalLength
                    } else {
                        box.lenses.add(Lense(label, focalLength))
                    }
                }

                else -> throw IllegalArgumentException("Unknown operation: $operation")

            }
        }
        return boxes.withIndex().sumOf { (boxIndex, box) ->
            box.lenses.withIndex().sumOf { (lenseIndex, lense) ->
                (boxIndex + 1) * (lenseIndex + 1) * lense.focalLength
            }
        }
    }
}

fun main() {
    val testInput = readInput("Day15_test", 2023)
    check(Day15.part1(testInput) == 1320)
    check(Day15.part2(testInput) == 145)

    val input = readInput("Day15", 2023)
    println(Day15.part1(input))
    println(Day15.part2(input))
}
