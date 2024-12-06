import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String, year: Int = 2021) = File("src/main/kotlin/aoc$year", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16).padStart(32, '0')


infix fun Int.modulo(mod: Int) = if (this > mod) this % mod else this
infix fun Long.modulo(mod: Long) = if (this > mod) this % mod else this

/**
 * Generates a "cartesian product" of two sequences
 *
 * @param other the sequence to 'mix' with this
 * @return a sequence of the cartesian product of [this] and [other]
 */
fun <T, U> Sequence<T>.withEachOf(other: Sequence<U>): Sequence<Pair<T, U>> = flatMap { t -> other.map { u -> t to u } }

/**
 * Data class representing a point on a 2D area
 */
@Deprecated("Use PointL class instead", ReplaceWith("PointL"))
data class Point(val x: Int, val y: Int) {

    private val actual = convert(this)

    private fun convert(original: PointL) = Point(original.x.toInt(), original.y.toInt())
    private fun convert(original: Point) = PointL(original.x.toLong(), original.y.toLong())

    /**
     * @param withDiagonal true to also include diagonal neighbours
     * @param validGrid optional area (pair of bottom left and upper right corner) in which the neighbours must be within
     * @return the neighbouring points of this point
     */
    fun getNeighbours(withDiagonal: Boolean = false, validGrid: Pair<Point, Point>? = null) =
        actual.getNeighbours(withDiagonal, validGrid?.let { convert(it.first) to convert(it.second) })
            .map { convert(it) }

    infix operator fun plus(other: Point) = convert(actual + convert(other))

    /**
     * @param dx the delta in x direction
     * @param dy the delta in y direction
     * @return the new resulting point, which is created by moving this point along the given vector
     */
    fun move(dx: Int, dy: Int) = convert(actual.move(dx, dy))

    fun isWithin(grid: Pair<Point, Point>) = actual.isWithin(convert(grid.first) to convert(grid.second))

    fun distanceTo(other: Point) = actual.distanceTo(convert(other)).toInt()

    fun longDistanceTo(other: Point) = actual.distanceTo(convert(other))
}


/**
 * Data class representing a point on a 2D area
 */
data class PointL(val x: Long, val y: Long) {

    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())

    /**
     * @param withDiagonal true to also include diagonal neighbours
     * @param validGrid optional area (pair of bottom left and upper right corner) in which the neighbours must be within
     * @return the neighbouring points of this point
     */
    fun getNeighbours(withDiagonal: Boolean = false, validGrid: Pair<PointL, PointL>? = null) = buildSet {
        for (i in -1..1) {
            for (j in -1..1) {
                if ((i.absoluteValue == j.absoluteValue) && (i == 0 || !withDiagonal)) {
                    continue
                }
                val p = move(i, j)
                if (validGrid == null || p.isWithin(validGrid)) {
                    add(p)
                }
            }
        }
    }

    infix operator fun plus(other: PointL) = move(other.x, other.y)

    /**
     * @param dx the delta in x direction
     * @param dy the delta in y direction
     * @return the new resulting point, which is created by moving this point along the given vector
     */
    fun move(dx: Long, dy: Long) = PointL(x + dx, y + dy)
    fun move(dx: Int, dy: Int) = move(dx.toLong(), dy.toLong())

    fun isWithin(grid: Pair<PointL, PointL>) =
        x >= grid.first.x && x <= grid.second.x && y >= grid.first.y && y <= grid.second.y

    fun distanceTo(other: PointL) = abs(x - other.x) + abs(y - other.y)
}

data class Point3(val x: Long, val y: Long, val z: Long) {
    companion object {
        fun fromCsvString(input: String): Point3 {
            val split = input.split(",")
            return Point3(split[0].toLong(), split[1].toLong(), split[2].toLong())
        }
    }

    constructor(x: Int, y: Int, z: Int) : this(x.toLong(), y.toLong(), z.toLong())

    infix operator fun plus(other: Point3) = move(other.x, other.y, other.z)

    fun move(dx: Long, dy: Long, dz: Long) = Point3(x + dx, y + dy, z + dz)
    fun move(dx: Int, dy: Int, dz: Int) = move(dx.toLong(), dy.toLong(), dz.toLong())
    fun distanceTo(other: Point3) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
}

/**
 * Shoelace formula to calculate the area enclosed by these points
 */
fun Iterable<PointL>.areaWithin(): Long {
    val actualList = if (this.last() != this.first()) {
        // make sure to close the border
        this + this.first()
    } else {
        this
    }
    return abs(
        actualList.windowed(2, 1)
            .sumOf { points -> (points.first().y + points.last().y) * (points.first().x - points.last().x) }) / 2L
}


/**
 * Splits a list into a list of lists by separating the original elements whenever an element matches the given predicate.
 * The matched element is *NOT* added to any sublist!
 *
 * @param predicate lambda to apply on an element to test if it should be a separator element
 * @return the list split into a list of lists by separating them at specific separator elements
 */
fun <T> List<T>.separateBy(predicate: (T) -> Boolean): List<List<T>> {
    val list = mutableListOf<List<T>>()
    var sublist = mutableListOf<T>()
    forEach {
        if (predicate(it)) {
            list.add(sublist)
            sublist = mutableListOf()
        } else {
            sublist.add(it)
        }
    }
    list.add(sublist)
    return list
}

/**
 * Finds a random common item in the given collections or throws a [NoSuchElementException] if there is none
 */
fun <T> findCommon(vararg collections: Collection<T>): T {
    var intersect = collections.first().toSet()
    collections.drop(1).map { it.toSet() }.forEach { intersect = intersect.intersect(it) }
    return intersect.first()
}

fun <T> Stack<T>.popTo(other: Stack<T>, count: Int = 1) {
    repeat(count) {
        val element = this.pop()
        other.add(element)
    }
}

inline fun <T> Iterable<T>.multiplyOf(selector: (T) -> Int): Int {
    var result = 1
    for (element in this) {
        result *= selector(element)
    }
    return result
}

inline fun <T> Iterable<T>.multiplyOfLong(selector: (T) -> Long): Long {
    var result = 1L
    for (element in this) {
        result *= selector(element)
    }
    return result
}

/**
 * Converts this list of strings into a 2D char array.
 * `array[0][0]` corresponds to the first character in the first string, `array[1][0]` to the second char in the first string
 * and so on.
 *
 * @param ignore optional character to ignore in the strings (for example a delimiter)
 */
fun List<String>.to2dCharArray(ignore: Char? = null): Array<CharArray> {
    val maxX = first().count { ignore == null || it != ignore } - 1
    val maxY = size
    val array = (0..maxX).asSequence().map { CharArray(maxY) }.toList().toTypedArray()
    withIndex().forEach { (y, row) ->
        row.filter { ignore == null || it != ignore }.withIndex().forEach { (x, char) ->
            array[x][y] = char
        }
    }
    return array
}

/**
 * @return the position of [char] or null, if it was not found
 */
fun Array<CharArray>.find(char: Char): PointL? {
    val maxX = first().size - 1
    val maxY = size - 1
    for (x in 0..<maxX) {
        for (y in 0..<maxY) {
            if (this[x][y] == char) {
                return PointL(x, y)
            }
        }
    }
    return null
}

inline fun <reified T : Any> List<String>.to2dArray(ignore: Char? = null, mapper: (Char) -> T) =
    to2dCharArray(ignore).map { chars -> chars.map(mapper).toTypedArray() }.toTypedArray()

inline fun <reified T : Any> List<String>.to2dArray(ignore: Char? = null, mapper: (Int, Int, Char) -> T) =
    to2dCharArray(ignore).withIndex()
        .map { (x, column) -> column.withIndex().map { (y, char) -> mapper(x, y, char) }.toTypedArray() }.toTypedArray()

fun List<String>.to2dIntArray(ignore: Char? = null) =
    to2dCharArray(ignore).map { chars -> chars.map { it.digitToInt() }.toIntArray() }.toTypedArray()

fun Array<CharArray>.print() {
    val maxX = size - 1
    val maxY = first().size - 1
    for (y in 0..maxY) {
        print("[")
        for (x in 0..maxX) {
            print(this[x][y])
        }
        println("]")
    }
}

/**
 * Removes the [other] range from this range (might produce 2 new ranges if [other] is somewhere in the middle of this)
 */
fun LongRange.cut(other: LongRange): Sequence<LongRange> = sequence {
    when {
        other.last < this@cut.first || this@cut.last < other.first -> yield(this@cut)  // completely out of cut
        other.first <= this@cut.first && this@cut.last <= other.last -> {} // completely within cut
        other.first <= this@cut.first && this@cut.last > other.last -> yield(LongRange(other.last + 1, this@cut.last))
        other.first > this@cut.first && this@cut.last <= other.last -> yield(LongRange(this@cut.first, other.first - 1))
        this@cut.first < other.first && other.last < this@cut.last -> {
            yield(LongRange(this@cut.first, other.first - 1))
            yield(LongRange(other.last + 1, this@cut.last))
        }

        else -> throw IllegalArgumentException("something wrong: $this@cut cut with $other")
    }
}

fun LongRange.overlaps(other: LongRange) = this.first <= other.last && other.first <= this.last

enum class Direction(val delta: PointL) {
    NORTH(PointL(0, -1)),
    EAST(PointL(1, 0)),
    SOUTH(PointL(0, 1)),
    WEST(PointL(-1, 0));

    fun rotateClockwise() = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }
}