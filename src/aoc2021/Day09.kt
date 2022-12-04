package aoc2021

import readInput

private class HeightMap(input: List<String>) {

    // heightmap is internally represented as a 2D Byte array
    private val data = input.map { str -> str.map { it.digitToInt().toByte() }.toByteArray() }.toTypedArray()

    /**
     * all the 'low points' in this height map
     */
    val lowPoints = sequence {
        for (x in data.indices) {
            for (y in data[x].indices) {
                val current = Point(x, y, data[x][y])
                val neighbours = getNeighboursOf(current)
                if (neighbours.all { it.height > current.height }) {
                    yield(current)
                }
            }
        }
    }

    /**
     * @param point a point within this height map to get the neighbouring points for
     * @return a list of all the neighbours of the given point (horizontal & vertical neighbours only)
     */
    fun getNeighboursOf(point: Point) = buildList {
        val x = point.x
        val y = point.y
        // points on the edged and the corners have only 2 or 3 neighbours
        if (x > 0) {
            add(Point(x - 1, y, data[x - 1][y]))
        }
        if (x < data.size - 1) {
            add(Point(x + 1, y, data[x + 1][y]))
        }
        if (y > 0) {
            add(Point(x, y - 1, data[x][y - 1]))
        }
        if (y < data[x].size - 1) {
            add(Point(x, y + 1, data[x][y + 1]))
        }
    }

    /**
     * Represents a point in a height map
     *
     * @property x the x-coordinate
     * @property y the y-coordinate
     * @property height the height at that point
     */
    data class Point(val x: Int, val y: Int, val height: Byte)
}

private fun part1(input: List<String>) = HeightMap(input).lowPoints.map { it.height + 1 }.sum()

private fun part2(input: List<String>): Int {
    val heightMap = HeightMap(input)
    return heightMap.lowPoints.map { lowPoint ->
        val basin = mutableSetOf<HeightMap.Point>()
        val queue = mutableListOf(lowPoint)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (basin.add(current)) {
                queue.addAll(heightMap.getNeighboursOf(current).filter { it.height < 9 })
            }
        }
        basin.count() // value of the basin is the amount of its points, ignoring their height
    }.sortedDescending().take(3).reduce { acc, i -> acc * i }
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
