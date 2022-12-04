package aoc2021

import readInput

@JvmInline
private value class BinaryString(val value: String)

private sealed class Packet<T>(val version: Int, protected val value: T) {

    /**
     * The sum of this packet's version and all its sub-packages (if any)
     */
    abstract val versionSum: Int

    /**
     * The value of the packet
     */
    abstract fun getValue(): Long
    override fun toString() = "${this::class.java.simpleName}(v$version) = $value"
}

private class LiteralPacket(version: Int, value: Long) : Packet<Long>(version, value) {
    override val versionSum = version

    companion object {
        fun parse(version: Int, payload: BinaryString): Pair<Packet<*>, BinaryString> {
            val valueString = StringBuilder()
            val remainingString = StringBuilder()
            var lastPart = false
            payload.value.windowed(5, 5, partialWindows = true).forEach {
                if (!lastPart) {
                    if (it.first() == '0') {
                        lastPart = true
                    }
                    valueString.append(it.drop(1))
                } else {
                    remainingString.append(it)
                }
            }
            return Pair(
                LiteralPacket(version, valueString.toString().toLong(2)), BinaryString(remainingString.toString())
            )
        }
    }

    override fun getValue() = value
}

private class OperatorPacket(version: Int, private val type: Int, value: List<Packet<*>>) :
    Packet<List<Packet<*>>>(version, value) {
    override val versionSum = version + value.sumOf { it.versionSum }

    companion object {
        fun parse(version: Int, id: Int, payload: BinaryString): Pair<Packet<*>, BinaryString> {
            val lengthTypeId = payload.value.first()
            val subPackets = mutableListOf<Packet<*>>()
            var remainingStr: String
            if (lengthTypeId == '0') {
                val totalLength = payload.value.substring(1 until 16).toInt(2)
                remainingStr = payload.value.substring(16 until 16 + totalLength)
                do {
                    val (packet, remainder) = decode(BinaryString(remainingStr))
                    subPackets.add(packet)
                    remainingStr = remainder.value
                } while (remainingStr.isNotBlank())
                remainingStr = payload.value.substring(16 + totalLength)
            } else {
                val subCount = payload.value.substring(1 until 12).toInt(2)
                remainingStr = payload.value.substring(12)
                for (i in 0 until subCount) {
                    val (packet, remainder) = decode(BinaryString(remainingStr))
                    subPackets.add(packet)
                    remainingStr = remainder.value
                }
            }
            return Pair(OperatorPacket(version, id, subPackets), BinaryString(remainingStr))
        }
    }

    override fun getValue() = when (type) {
        0 -> value.sumOf { it.getValue() }
        1 -> value.fold(1L) { acc, packet -> acc * packet.getValue() }
        2 -> value.minOf { it.getValue() }
        3 -> value.maxOf { it.getValue() }
        5 -> if (value[0].getValue() > value[1].getValue()) 1 else 0
        6 -> if (value[0].getValue() < value[1].getValue()) 1 else 0
        7 -> if (value[0].getValue() == value[1].getValue()) 1 else 0
        else -> throw IllegalArgumentException("Type $type is not a valid operator packet type!")
    }
}

/**
 * @return this hex string as a binary string with padded 0 as prefix
 */
private fun String.hexToBinary(): BinaryString {
    val expectedSize = this.length * 4
    val result = toBigInteger(16).toString(2)
    val missingZeros = expectedSize - result.length
    return BinaryString((0 until missingZeros).joinToString(separator = "", postfix = result) { "0" })
}

/**
 * Decodes the given BITS hex string
 */
private fun decode(input: String): Packet<*> {
    val re = decode(input.hexToBinary()).first
    println("$input --> $re (${re.versionSum})")
    return re
}

/**
 * Decodes the given binary string into a BITS packet and a remaining bit string
 */
private fun decode(input: BinaryString): Pair<Packet<*>, BinaryString> {
    val binaryStr = input.value
    val version = binaryStr.take(3).toInt(2)
    val type = binaryStr.substring(3 until 6).toInt(2)
    val payload = BinaryString(binaryStr.substring(6))
    val result = if (type == 4) {
        LiteralPacket.parse(version, payload)
    } else {
        OperatorPacket.parse(version, type, payload)
    }
    return result
}

private fun part1(input: List<String>) = input.map { decode(it).versionSum }

private fun part2(input: List<String>) = decode(input.first()).getValue()

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == listOf(16, 12, 23, 31))

    val input = readInput("Day16")
    println(part1(input).first())
    println(part2(input))
}
