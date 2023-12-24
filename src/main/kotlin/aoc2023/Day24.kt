package aoc2023

import PointL
import readInput
import withEachOf
import java.lang.IllegalArgumentException
import kotlin.math.abs

object Day24 {

    private data class Hailstone(val position: PointL, val velocity: PointL) {
        companion object {
            // 19, 13, 30 @ -2,  1, -2
            private val regex =
                """(?<x>-?\d+),\s+(?<y>-?\d+),\s+(?<z>-?\d+)\s+@\s+(?<dx>-?\d+),\s+(?<dy>-?\d+),\s+(?<dz>-?\d+)""".toRegex()

            fun fromString(input: String): Hailstone {
                val match = regex.matchEntire(input)
                if (match != null) {
                    val get = { key: String -> match.groups[key]!!.value.toLong() }
                    return Hailstone(PointL(get("x"), get("y")), PointL(get("dx"), get("dy")))
                } else throw IllegalArgumentException("Does not match: $input")
            }
        }

        //
        private val b = ((position.x / velocity.x.toDouble())) * -velocity.y.toDouble() + position.y
        private val a = velocity.y / velocity.x.toDouble()

        /**
         * @return the point where this and [other] intersect or null, if they never do
         */
        fun intersect(other: Hailstone): Pair<Double, Double>? {
            if (this.a == other.a) return null
            // intersection: x, y
            // -> this.a * x + this.b = y
            // -> other.a * x + other.b = y
            // -> this.a * x + this.b = other.a * x + other.b
            // -> this.a * x + this.b - other.a * x = other.b
            // -> (this.a - other.a) * x + this.b = other.b
            // -> (this.a - other.a) * x = other.b - this.b
            // -> x = (other.b - this.b) / (this.a - other.a)
            val x = (other.b - this.b) / (this.a - other.a)
            val y = this.a * x + this.b
            return x to y
        }

        /**
         * @return true, if the [point] was already past some time ago by this hailstone
         */
        fun inThePast(point: Pair<Double, Double>) =
            abs(position.x + velocity.x - point.first) > abs(position.x - point.first)
    }

    fun part1(input: List<String>, min: Long, max: Long): Int {
        val range = min.toDouble()..max.toDouble()
        val hailstones = input.map { Hailstone.fromString(it) }.asSequence()
        return hailstones.withEachOf(hailstones)
            .filter { it.first != it.second }
            .filter {
                val intersection = it.first.intersect(it.second)
                intersection != null // they do intersect...
                        && intersection.first in range && intersection.second in range // ...in the given range
                        && !it.first.inThePast(intersection) && !it.second.inThePast(intersection)// ...in the future
            }
            .count() / 2 // if A hits B, then B also hits A -> divide result by 2
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day24_test", 2023)
    check(Day24.part1(testInput, 7L, 27L) == 2)
    check(Day24.part2(testInput) == 0)

    val input = readInput("Day24", 2023)
    println(Day24.part1(input, 200000000000000L, 400000000000000L))
    println(Day24.part2(input))
}
