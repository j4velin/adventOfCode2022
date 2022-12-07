package aoc2022

import readInput

sealed class FileSystemElement(val name: String) {
    abstract val size: Int
}

class Directory(name: String, val parent: Directory?) : FileSystemElement(name) {
    override val size: Int
        get() = elements.sumOf { it.size }
    val elements = mutableSetOf<FileSystemElement>()
    override fun toString(): String {
        return "- $name (dir)\n" + elements.joinToString(separator = "\n") { "  $it" }
            .split("\n").joinToString(separator = "\n") { "  $it" }
    }
}

class File(name: String, override val size: Int) : FileSystemElement(name) {
    override fun toString(): String {
        return "- $name (file, size=$size)"
    }
}


fun main() {

    /**
     * Parses the given [input] into a root [Directory] structure, from which all other [FileSystemElement]s are reachable.
     */
    fun parseDirectoryStructure(input: List<String>): Directory {
        val root = Directory("/", null)
        var currentDirectory = root
        input.forEach {
            when {
                it.startsWith("$ cd /") -> currentDirectory = root
                it.startsWith("$ cd ..") -> currentDirectory = currentDirectory.parent ?: root
                it.startsWith("$ cd ") -> currentDirectory =
                    currentDirectory.elements.filterIsInstance<Directory>().first { e -> e.name == it.substring(5) }
                it.startsWith("$ ls") -> {} // ignore
                it.startsWith("dir ") -> currentDirectory.elements.add(Directory(it.substring(4), currentDirectory))
                else -> {
                    val (size: String, name: String) = it.split(" ")
                    currentDirectory.elements.add(File(name, size.toInt()))
                }
            }
        }
        return root
    }

    /**
     * Finds all the directories under the given [root] which matches the given [predicate]
     */
    fun findDir(root: Directory, predicate: (Directory) -> Boolean): Set<Directory> {
        val foundDirs = mutableSetOf<Directory>()
        val dirsToCheck = mutableSetOf(root)
        while (dirsToCheck.isNotEmpty()) {
            val current = dirsToCheck.first()
            if (predicate(current)) {
                foundDirs.add(current)
            }
            dirsToCheck.remove(current)
            dirsToCheck.addAll(current.elements.filterIsInstance<Directory>())
        }
        return foundDirs
    }

    fun part1(input: List<String>): Int {
        val root = parseDirectoryStructure(input)
        return findDir(root) { it.size <= 100000 }.sumOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val root = parseDirectoryStructure(input)
        val total = 70000000
        val required = 30000000
        val free = total - root.size
        val targetSize = required - free
        return findDir(root) { it.size >= targetSize }.minOf { it.size }
    }

    val testInput = readInput("Day07_test", 2022)
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07", 2022)
    println(part1(input))
    println(part2(input))
}
