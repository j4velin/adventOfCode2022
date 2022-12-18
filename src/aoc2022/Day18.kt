package aoc2022

import readInput

private data class Point3(val x: Int, val y: Int, val z: Int) {
    companion object {
        fun fromString(input: String): Point3 {
            val split = input.split(",").map { it.toInt() }
            return Point3(split[0], split[1], split[2])
        }
    }

    val neighbours by lazy {
        buildSet {
            add(Point3(x - 1, y, z))
            add(Point3(x + 1, y, z))
            add(Point3(x, y - 1, z))
            add(Point3(x, y + 1, z))
            add(Point3(x, y, z - 1))
            add(Point3(x, y, z + 1))
        }
    }

    fun getExposedSides(points: Collection<Point3>) = 6 - neighbours.count { points.contains(it) }
}

private data class LavaDroplet(val shapePoints: Collection<Point3>) {
    val surfaceArea by lazy { shapePoints.sumOf { it.getExposedSides(shapePoints) } }
    val exteriorSurfaceArea by lazy {
        /*
        TODO
        val max = Point3(shapePoints.maxOf { it.x }, shapePoints.maxOf { it.y }, shapePoints.maxOf { it.z })
        val maxVolume = max.x * max.y * max.z
        for (x in 0..max.x) {
            for (y in 0..max.y) {
                for (z in 0..max.z) {

                }
            }
        }
         */
        0
    }
}

private fun part1(input: List<String>): Int {
    val droplet = LavaDroplet(input.map { Point3.fromString(it) })
    return droplet.surfaceArea
}

private fun part2(input: List<String>): Int {
    val droplet = LavaDroplet(input.map { Point3.fromString(it) })
    return droplet.exteriorSurfaceArea
}

fun main() {
    val testInput = readInput("Day18_test", 2022)
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18", 2022)
    println(part1(input))
    println(part2(input))
}
