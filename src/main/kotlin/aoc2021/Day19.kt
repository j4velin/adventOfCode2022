package aoc2021

import readInput
import kotlin.math.abs
import kotlin.math.max

private enum class Axis { X, Y, Z }

private const val OVERLAPPING_BEACONS_REQUIRED = 12

/**
 * A beacon scanner
 *
 * @property name the name of the scanner
 * @property beacons the position of all beacons within range of this scanner, with their position relative to the
 * scanners center
 */
private data class Scanner(val name: String, val beacons: Collection<Vector>) {

    companion object {
        /**
         * Parses the scanner from some string lines
         */
        fun parseString(input: List<String>) = Scanner(input.first().replace("---", "").trim(),
            input.drop(1).map { line -> line.split(",").map { it.toInt() }.toIntArray() }.map { Vector(it) }
                .toMutableSet()
        )

        /**
         * Tries to overlay the beacons in the given [scanner] with given [beacons] to find a 'movement' vector, by which
         * the center of the [scanner] must be moved to align with at least [OVERLAPPING_BEACONS_REQUIRED] [beacons]
         *
         * @param scanner the scanner to check
         * @param beacons the existing beacons
         * @return a vector by which the [scanner] must be moved so that at least [OVERLAPPING_BEACONS_REQUIRED] beacons
         * are at an identical position in with the given [beacons], or null if not enough overlapping beacons could be found
         */
        private fun getMovementToOverlapBeacons(scanner: Scanner, beacons: Collection<Vector>): Vector? {
            for (beacon1 in beacons) {
                val fromBeacon1 = beacons.map { beacon1 - it }.toSet()
                for (beacon2 in scanner.beacons) {
                    val fromBeacon2 = scanner.beacons.map { beacon2 - it }.toSet()
                    val common = fromBeacon1.intersect(fromBeacon2)
                    if (common.size >= OVERLAPPING_BEACONS_REQUIRED) {
                        return beacon1 - beacon2
                    }
                }
            }
            return null
        }

        /**
         * Tries to locate the given scanner
         *
         * @param scanner the scanner to search for
         * @param beacons the currently beacons
         * @return a pair containing the scanner's position and the new beacon locations, or null, if the scanner can not
         * be located with the given world data yet
         */
        fun tryLocateScanner(scanner: Scanner, beacons: Collection<Vector>) =
            // check all possible orientations of the scanner and see if in any we can overlay the required number of beacons
            scanner.getOrientations().firstNotNullOfOrNull { rotatedScanner ->
                getMovementToOverlapBeacons(rotatedScanner, beacons)?.let { newCenter ->
                    Pair(newCenter, rotatedScanner.beacons.map { it + newCenter }.filterNot { it in beacons })
                }
            }
    }

    private fun rotate(axis: Axis) = Scanner(name, beacons.map { it.rotate(axis) }.toMutableSet())

    private fun getOrientations(): Sequence<Scanner> {
        // this can probably be optimized...
        var current = this
        val set = sequence {
            yield(current)
            Axis.values().forEach { axis ->
                current = current.rotate(axis)
                yield(current)
                Axis.values().forEach { axis2 ->
                    current = current.rotate(axis2)
                    yield(current)
                    Axis.values().forEach { axis3 ->
                        current = current.rotate(axis3)
                        yield(current)
                        Axis.values().forEach { axis4 ->
                            current = current.rotate(axis4)
                            yield(current)
                        }
                    }
                }
            }
        }
        return set.distinct()
    }

    override fun toString() = name
}

private data class Vector(val x: Int, val y: Int, val z: Int) {
    constructor(array: IntArray) : this(array[0], array[1], array[2])

    fun rotate(axis: Axis) = when (axis) {
        Axis.X -> Vector(x, -z, y)
        Axis.Y -> Vector(z, y, -x)
        Axis.Z -> Vector(-y, x, z)
    }

    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y, z - other.z)

    fun manhattanDistanceTo(other: Vector) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

    override fun toString() = "$x,$y,$z"
}

private data class World(val scanners: Collection<Vector>, val beacons: Collection<Vector>) {
    companion object {
        fun fromScannerData(input: List<String>): World {
            val scanners = parseAllScanners(input)
            val origin = scanners.first()
            val scannersToLocate = scanners.drop(1).toMutableSet()
            val beacons = origin.beacons.toMutableList()
            val scannerPositions = mutableListOf(Vector(0, 0, 0))
            while (scannersToLocate.isNotEmpty()) {
                var newScannerFound = false
                val iterator = scannersToLocate.iterator()
                while (iterator.hasNext()) {
                    val scanner = iterator.next()
                    Scanner.tryLocateScanner(scanner, beacons)?.let { (scannerPosition, newBeacons) ->
                        println("Found $scanner at $scannerPosition (adding ${newBeacons.size} new beacons)")
                        scannerPositions.add(scannerPosition)
                        beacons.addAll(newBeacons)
                        iterator.remove()
                        newScannerFound = true
                    }
                }
                if (!newScannerFound) break
            }
            return World(scannerPositions, beacons)
        }

        /**
         * Parses the input into a list of scanners with beacons relative to their center
         */
        private fun parseAllScanners(input: List<String>): List<Scanner> {
            val scannerInput = mutableListOf<String>()
            return buildList {
                for (line in input) {
                    if (line.isBlank()) {
                        add(Scanner.parseString(scannerInput))
                        scannerInput.clear()
                    } else {
                        scannerInput.add(line)
                    }
                }
                // input file does not end with a blank line
                if (scannerInput.isNotEmpty()) {
                    add(Scanner.parseString(scannerInput))
                }
            }
        }
    }
}

private fun part1(world: World) = world.beacons.size

private fun part2(world: World): Int {
    val scanners = world.scanners
    var maxDistance = 0
    for (s1 in scanners) {
        for (s2 in scanners) {
            maxDistance = max(maxDistance, s1.manhattanDistanceTo(s2))
        }
    }
    return maxDistance
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = World.fromScannerData(readInput("Day19_test"))

    check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    val input = World.fromScannerData(readInput("Day19"))

    println(part1(input))
    println(part2(input))
}
