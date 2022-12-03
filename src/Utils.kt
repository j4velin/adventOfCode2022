import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

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