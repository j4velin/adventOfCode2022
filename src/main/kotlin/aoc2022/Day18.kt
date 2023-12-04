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

    fun getExteriorSurfaceSides(isOutSide: (Point3) -> Boolean) = neighbours.count { isOutSide(it) }
}

private data class LavaDroplet(private val shapePoints: Collection<Point3>) {
    val surfaceArea by lazy { shapePoints.sumOf { it.getExposedSides(shapePoints) } }
    val exteriorSurfaceArea by lazy {
        val max = Point3(shapePoints.maxOf { it.x }, shapePoints.maxOf { it.y }, shapePoints.maxOf { it.z })

        val rangeX = 0..max.x
        val rangeY = 0..max.y
        val rangeZ = 0..max.z
        val outsideCubes = mutableSetOf<Point3>()
        val isOutside: (Point3) -> Boolean =
            { p -> outsideCubes.contains(p) || !rangeX.contains(p.x) || !rangeY.contains(p.y) || !rangeZ.contains(p.z) }

        for (x in max.x downTo 0) {
            for (y in max.y downTo 0) {
                for (z in max.z downTo 0) {
                    val point = Point3(x, y, z)
                    if (!shapePoints.contains(point)) {
                        // empty (inside or outside)
                        val uncheckedNeighbours = point.neighbours.filter { !shapePoints.contains(it) }.toMutableSet()
                        val emptyConnectedPoints = mutableSetOf(point)
                        while (uncheckedNeighbours.isNotEmpty()) {
                            val n = uncheckedNeighbours.random()
                            uncheckedNeighbours.remove(n)
                            if (isOutside(n)) {
                                outsideCubes.addAll(emptyConnectedPoints)
                                break
                            } else {
                                emptyConnectedPoints.add(n)
                                uncheckedNeighbours.addAll(n.neighbours.filter { !shapePoints.contains(it) }
                                    .filter { !uncheckedNeighbours.contains(it) && !emptyConnectedPoints.contains(it) })
                            }
                        }
                    }
                }
            }
        }

        shapePoints.sumOf { it.getExteriorSurfaceSides(isOutside) }
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
