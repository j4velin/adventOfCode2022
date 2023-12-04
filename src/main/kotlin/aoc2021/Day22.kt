package aoc2021

import readInput
import java.util.*
import kotlin.math.max
import kotlin.math.min

private data class Instruction(
    val newState: Boolean,
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    companion object {
        fun fromString(input: String): Instruction {
            val split = input.split(" ")
            val ranges = split[1].trim().split(",")
            return Instruction(
                split[0].trim() == "on",
                rangeFromString(ranges[0]),
                rangeFromString(ranges[1]),
                rangeFromString(ranges[2])
            )
        }
    }
}

private fun rangeFromString(input: String): IntRange {
    // x=-20..26,
    val split = input.drop(2).removeSuffix(",").split("..")
    return IntRange(split[0].toInt(), split[1].toInt())
}

/**
 * Applies the given instructions on a part of the reactor
 * @param instructions the instructions to apply
 * @param xRange the range in x dimension to consider
 * @param yRange the range in y dimension to consider (default: same as [xRange])
 * @param zRange the range in z dimension to consider (default: same as [xRange])
 * @return the number of turned on cubes after applying all instructions on this subset
 */
private fun applyInstructions(
    instructions: List<Instruction>, xRange: IntRange, yRange: IntRange = xRange, zRange: IntRange = xRange
): Int {
    val size = xRange.count().toLong() * yRange.count() * zRange.count()
    if (size < 0 || size > Int.MAX_VALUE) {
        throw IllegalArgumentException("Range too big")
    }
    val xOffset = if (xRange.first < 0) xRange.first * -1 else 0
    val yOffset = if (yRange.first < 0) yRange.first * -1 else 0
    val zOffset = if (zRange.first < 0) zRange.first * -1 else 0
    val reactor = BitSet(size.toInt())
    val yMultiplier = xRange.count()
    val zMultiplier = xRange.count() * yRange.count()
    instructions.filter { it.xRange.intersect(xRange).isNotEmpty() }.filter { it.yRange.intersect(yRange).isNotEmpty() }
        .filter { it.zRange.intersect(zRange).isNotEmpty() }.forEach {
            for (x in it.xRange) {
                for (y in it.yRange) {
                    for (z in it.zRange) {
                        val xIndex = x + xOffset
                        val yIndex = y + yOffset
                        val zIndex = z + zOffset
                        val index = xIndex + yIndex * yMultiplier + zIndex * zMultiplier
                        reactor[index] = it.newState
                    }
                }
            }
        }
    return reactor.cardinality()
}

private fun part1(input: List<String>) =
    applyInstructions(input.map(Instruction.Companion::fromString), IntRange(-50, 50))


private data class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange) {

    private val isEmpty = x.isEmpty() || y.isEmpty() || z.isEmpty()
    val cubesWithIn = x.count().toLong() * y.count() * z.count()

    /**
     * @return true, if the [other] cuboid overlaps with this one (e.g. has at least one cube in common)
     */
    fun overlapsWith(other: Cuboid) =
        (x.last > other.x.first && x.first < other.x.last) &&
                (y.last > other.y.first && y.first < other.y.last) &&
                (z.last > other.z.first && z.first < other.z.last)

    /**
     * @return true, if the [other] cuboid is contained within this cuboid
     */
    fun contains(other: Cuboid) =
        x.first <= other.x.first && x.last >= other.x.last &&
                y.first <= other.y.first && y.last >= other.y.last &&
                z.first <= other.z.first && z.last >= other.z.last

    /**
     * Removes the cubes which intersect in this cuboid and the [cut] cuboid
     * @return a set of smaller [Cuboid]s, which are created by "cutting" the cubes of [cut] which intersect with this [Cuboid]
     */
    fun cut(cut: Cuboid): Collection<Cuboid> {
        val z1 = Cuboid(x, y, z.first until cut.z.first)
        val z2 = Cuboid(x, y, cut.z.last + 1..z.last)

        val y1 = Cuboid(x, y.first until cut.y.first, max(z.first, cut.z.first)..min(z.last, cut.z.last))
        val y2 = Cuboid(x, cut.y.last + 1..y.last, max(z.first, cut.z.first)..min(z.last, cut.z.last))

        val x1 = Cuboid(
            x.first until cut.x.first,
            max(y.first, cut.y.first)..min(y.last, cut.y.last),
            max(z.first, cut.z.first)..min(z.last, cut.z.last)
        )
        val x2 = Cuboid(
            cut.x.last + 1..x.last,
            max(y.first, cut.y.first)..min(y.last, cut.y.last),
            max(z.first, cut.z.first)..min(z.last, cut.z.last)
        )

        return setOf(x1, x2, y1, y2, z1, z2).filterNot { it.isEmpty }
    }
}

/**
 * Adds the new cubes contained within [newCuboid] to the set of distinct [Cuboid]s
 *
 * @param existing the existing cuboids
 * @param newCuboid the new cuboid to add
 * @return a set of cuboids, which do not have any intersecting cubes
 */
private fun addCuboid(existing: Set<Cuboid>, newCuboid: Cuboid): Set<Cuboid> {
    val overlap = existing.find { it.overlapsWith(newCuboid) }
    return if (overlap == null) {
        existing.toMutableSet().also { it.add(newCuboid) }
    } else {
        val newCuboids = newCuboid.cut(overlap)
        if (newCuboids.isEmpty()) {
            existing
        } else {
            newCuboids.flatMap { addCuboid(existing, it) }.toSet()
        }
    }
}

/**
 * @param instruction instruction to apply
 * @param cuboids a set of cuboids which contain the cubes which are turned on
 * @return a new set of cuboids with the cubes that are turned on after applying [instruction]
 */
private fun applyInstruction(instruction: Instruction, cuboids: Set<Cuboid>): Set<Cuboid> {
    val newCuboid = Cuboid(instruction.xRange, instruction.yRange, instruction.zRange)
    return if (instruction.newState) { // instruction will turn on some cubes
        addCuboid(cuboids, newCuboid)
    } else { // instruction will turn off some cubes
        cuboids.flatMap {
            if (it.overlapsWith(newCuboid)) {
                it.cut(newCuboid)
            } else {
                setOf(it)
            }
        }.toSet()
    }
}

private fun part2(input: List<String>): Long {
    val instructions = input.map(Instruction.Companion::fromString)
    var cuboids = emptySet<Cuboid>()
    instructions.forEach { cuboids = applyInstruction(it, cuboids) }
    return cuboids.sumOf { it.cubesWithIn }
}


fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 474140)
    check(part2(testInput) == 2758514936282235L)

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}
