package aoc2021

import readInput
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

private sealed class SnailfishNumber(var parent: SnailfishPair?) {
    protected abstract fun explode(currentLevel: Int): Boolean
    abstract fun split(): Boolean

    abstract fun getMostRightPrimitive(): SnailfishPrimitive
    abstract fun getMostLeftPrimitive(): SnailfishPrimitive

    fun reduce(): SnailfishNumber {
        var changed: Boolean
        do {
            do {
                changed = explode(0)
            } while (changed)
            changed = split()
        } while (changed)
        return this
    }

    protected fun replaceMeWith(other: SnailfishNumber) {
        if (parent?.left == this) {
            parent?.left = other
        } else if (parent?.right == this) {
            parent?.right = other
        }
        if (this is SnailfishPair && other is SnailfishPair) {
            this.left.parent = other
            this.right.parent = other
        }
    }

    protected fun getRoot(): SnailfishNumber = (parent as SnailfishNumber?)?.getRoot() ?: this

    operator fun plus(other: SnailfishNumber): SnailfishPair {
        var root = getRoot()
        val result = SnailfishPair(this, other, parent)
        if (parent == null) {
            // we are already the root -> create new one
            root = result
            this.parent = root
        } else {
            replaceMeWith(result)
        }
        other.parent = result
        root.reduce()

        return result
    }

    abstract fun getMagnitude(): Int

    companion object {
        fun parseString(input: String): SnailfishNumber {
            if (input.startsWith("[")) {
                var braces = 0
                for (i in input.withIndex()) {
                    val c = i.value
                    if (c == '[') {
                        braces++
                    } else if (c == ']') {
                        braces--
                    } else if (c == ',' && braces == 1) {
                        val left = parseString(input.substring(1, i.index))
                        val right = parseString(input.substring(i.index + 1, input.length - 1))
                        val newPair = SnailfishPair(left, right, null)
                        newPair.right.parent = newPair
                        newPair.left.parent = newPair
                        return newPair
                    }
                }
                throw IllegalArgumentException("Can not parse Snailfish number for input '$input'")
            } else {
                return SnailfishPrimitive(input.toInt(), null)
            }
        }
    }

}

private class SnailfishPrimitive(var value: Int, parent: SnailfishPair?) : SnailfishNumber(parent) {
    override fun explode(currentLevel: Int) = false

    override fun split(): Boolean {
        if (value > 9) {
            val left = SnailfishPrimitive(floor(value / 2f).toInt(), null)
            val right = SnailfishPrimitive(ceil(value / 2f).toInt(), null)
            val newNumber = SnailfishPair(left, right, parent)
            left.parent = newNumber
            right.parent = newNumber
            replaceMeWith(newNumber)
            return true
        }
        return false
    }

    override fun getMostRightPrimitive() = this
    override fun getMostLeftPrimitive() = this
    override fun getMagnitude() = value
    override fun toString() = "$value"
}

private class SnailfishPair(var left: SnailfishNumber, var right: SnailfishNumber, parent: SnailfishPair?) :
    SnailfishNumber(parent) {

    override fun explode(currentLevel: Int): Boolean {
        return if (currentLevel >= 4) {
            val leftNeighbour = parent?.getLeftNeighbourOf(this)
            val rightNeighbour = parent?.getRightNeighbourOf(this)

            if (leftNeighbour != null) {
                leftNeighbour.value += (left as SnailfishPrimitive).value
            } else {
                parent!!.left = SnailfishPrimitive(0, parent)
            }

            if (rightNeighbour != null) {
                rightNeighbour.value += (right as SnailfishPrimitive).value
            } else {
                parent!!.right = SnailfishPrimitive(0, parent)
            }

            if (rightNeighbour != null && leftNeighbour != null) {
                if (parent?.left == this) {
                    parent?.left = SnailfishPrimitive(0, parent)
                } else if (parent?.right == this) {
                    parent?.right = SnailfishPrimitive(0, parent)
                }
            }
            true
        } else if ((left as? SnailfishPair)?.explode(currentLevel + 1) == true) {
            true
        } else {
            (right as? SnailfishPair)?.explode(currentLevel + 1) == true
        }
    }

    private fun getLeftNeighbourOf(child: SnailfishPair): SnailfishPrimitive? {
        return if (child == right) {
            if (left is SnailfishPair) {
                left.getMostRightPrimitive()
            } else {
                left as SnailfishPrimitive
            }
        } else if (child == left) {
            parent?.getLeftNeighbourOf(this)
        } else {
            throw IllegalArgumentException("Number $child is not a child of $this")
        }
    }

    private fun getRightNeighbourOf(child: SnailfishPair): SnailfishPrimitive? {
        return if (child == right) {
            parent?.getRightNeighbourOf(this)
        } else if (child == left) {
            if (right is SnailfishPair) {
                right.getMostLeftPrimitive()
            } else {
                right as SnailfishPrimitive
            }
        } else {
            throw IllegalArgumentException("Number $child is not a child of $this")
        }
    }

    override fun getMostRightPrimitive() = right.getMostRightPrimitive()
    override fun getMostLeftPrimitive() = left.getMostLeftPrimitive()
    override fun getMagnitude() = 3 * left.getMagnitude() + 2 * right.getMagnitude()
    override fun split() = left.split() || right.split()
    override fun toString() = "[$left,$right]"
}

private fun addAll(input: List<String>) = input.map { SnailfishNumber.parseString(it) }
    .fold(null) { acc: SnailfishNumber?, other: SnailfishNumber -> acc?.plus(other) ?: other }

private fun part1(input: List<String>) = addAll(input)?.getMagnitude() ?: 0

private fun part2(input: List<String>): Int {
    var max = 0
    for (i in input.indices) {
        for (j in input.indices) {
            if (i != j) {
                val n1 = SnailfishNumber.parseString(input[i])
                val n2 = SnailfishNumber.parseString(input[j])
                max = max(max, (n1 + n2).getMagnitude())
            }
        }
    }
    return max
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")

    check((SnailfishNumber.parseString("[1,2]") + SnailfishNumber.parseString("[[3,4],5]")).toString() == "[[1,2],[[3,4],5]]")
    check(SnailfishNumber.parseString("[[[[[9,8],1],2],3],4]").reduce().toString() == "[[[[0,9],2],3],4]")
    check(SnailfishNumber.parseString("[7,[6,[5,[4,[3,2]]]]]").reduce().toString() == "[7,[6,[5,[7,0]]]]")
    check(SnailfishNumber.parseString("[[6,[5,[4,[3,2]]]],1]").reduce().toString() == "[[6,[5,[7,0]]],3]")
    // slightly different expected result as our API only provide a "reduce" method (and even that should be private)
    check(
        SnailfishNumber.parseString("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").reduce()
            .toString() == "[[3,[2,[8,0]]],[9,[5,[7,0]]]]"
    )
    check(
        SnailfishNumber.parseString("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]").reduce()
            .toString() == "[[3,[2,[8,0]]],[9,[5,[7,0]]]]"
    )
    check((SnailfishNumber.parseString("[[[[4,3],4],4],[7,[[8,4],9]]]") + SnailfishNumber.parseString("[1,1]")).toString() == "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")

    check(addAll(listOf("[1,1]", "[2,2]", "[3,3]", "[4,4]")).toString() == "[[[[1,1],[2,2]],[3,3]],[4,4]]")
    check(addAll(testInput).toString() == "[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]")

    check(SnailfishNumber.parseString("[[1,2],[[3,4],5]]").getMagnitude() == 143)

    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
