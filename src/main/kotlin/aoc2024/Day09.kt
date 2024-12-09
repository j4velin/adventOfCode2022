package aoc2024

import readInput

object Day09 {

    private enum class State { FILE, FREE }

    fun part1(input: List<String>): Long {
        var currentId = -1
        var currentState = State.FREE
        val diskLayout = input.joinToString(separator = "").flatMap { char ->
            val blocksize = char.digitToInt()
            currentState = if (currentState == State.FREE) State.FILE else State.FREE
            if (currentState == State.FILE) currentId++
            buildList {
                repeat(blocksize) {
                    if (currentState == State.FREE) add(-1) else add(currentId)
                }
            }
        }.toMutableList()

        var rightIndex = diskLayout.size - 1
        for (i in diskLayout.indices) {
            if (diskLayout[i] == -1) {
                while (rightIndex > i && diskLayout[rightIndex] == -1) {
                    rightIndex--
                }
                diskLayout[i] = diskLayout[rightIndex]
                diskLayout[rightIndex] = -1
            }
        }

        var checksum = 0L
        diskLayout.withIndex().filter { it.value >= 0 }
            .forEach { (index, value) -> checksum += index.toLong() * value }
        return checksum
    }

    private sealed class Element(val size: Int)
    private class Free(size: Int) : Element(size)
    private class File(size: Int, val id: Int) : Element(size)

    fun part2(input: List<String>): Long {
        var currentId = -1
        var currentState = State.FREE
        val diskLayout = input.joinToString(separator = "").map { char ->
            val blocksize = char.digitToInt()
            currentState = if (currentState == State.FREE) State.FILE else State.FREE
            if (currentState == State.FILE) {
                currentId++
                File(size = blocksize, id = currentId)
            } else {
                Free(size = blocksize)
            }
        }.toMutableList()

        var rightIndex = diskLayout.size
        var minMoved = Integer.MAX_VALUE
        while (rightIndex > 0) {
            rightIndex--
            val elementToCheck = diskLayout[rightIndex]
            if (rightIndex > 0 && elementToCheck is File && elementToCheck.id < minMoved) {

                val requiredSpace = elementToCheck.size

                var leftIndex = 0
                while (leftIndex < diskLayout.size && (diskLayout[leftIndex] !is Free || diskLayout[leftIndex].size < requiredSpace)) {
                    leftIndex++
                }
                if (leftIndex < diskLayout.size && leftIndex < rightIndex) {
                    val freeSpace = diskLayout[leftIndex].size
                    if (requiredSpace <= freeSpace) {
                        minMoved = elementToCheck.id
                        diskLayout.set(index = rightIndex, element = Free(size = requiredSpace))
                        diskLayout.set(index = leftIndex, element = elementToCheck)
                        val spaceLeft = freeSpace - requiredSpace
                        var added = 0
                        if (spaceLeft > 0) {
                            added = 1
                            diskLayout.add(
                                index = leftIndex + 1,
                                element = Free(size = freeSpace - requiredSpace)
                            )
                        }

                        // combine new free space areas
                        var distance = 1
                        val combine: (Int) -> Boolean = { otherIndex ->
                            if (otherIndex < diskLayout.size && diskLayout[otherIndex] is Free) {
                                val newFree =
                                    diskLayout[otherIndex].size + diskLayout[rightIndex + added].size
                                // keep one with size=0 so we don't need to manage new indices again
                                diskLayout.set(
                                    index = otherIndex,
                                    element = Free(size = 0)
                                )
                                diskLayout.set(
                                    index = rightIndex + added,
                                    element = Free(size = newFree)
                                )
                                true
                            } else {
                                false
                            }
                        }
                        // TODO: we should probably increase the distance to check if there are more areas to consolidate
                        // but somehow that fails on my input and it already works with just one combining loop...
                        combine(rightIndex - distance + added)
                        combine(rightIndex + distance + added)
                    }
                }
            }
        }
        var checksum = 0L
        var index = 0L
        diskLayout.filter { it.size > 0 }.forEach { element ->
            repeat(element.size) {
                if (element is File) {
                    checksum += index * element.id
                }
                index++
            }
        }
        return checksum
    }
}

fun main() {
    val testInput = readInput("Day09_test", 2024)
    check(Day09.part1(testInput) == 1928L)
    check(Day09.part2(testInput) == 2858L)

    val input = readInput("Day09", 2024)
    println(Day09.part1(input))
    println(Day09.part2(input))
}
