package aoc2022

import Point
import readInput

data class Tree(val position: Point, val height: Int)

fun main() {

    fun getAllTrees(map: Array<IntArray>): Set<Tree> {
        val trees = mutableSetOf<Tree>()
        for (y in map.indices) {
            for (x in map[0].indices) {
                trees.add(Tree(Point(x, y), map[y][x]))
            }
        }
        return trees
    }

    fun isTreeVisible(map: Array<IntArray>, tree: Tree): Boolean {
        // outter rows and columns are always visible
        if (tree.position.x == 0 || tree.position.y == 0 || tree.position.x == map[0].size - 1 || tree.position.y == map.size - 1) {
            return true
        } else {
            // search row
            var fromLeft = true
            for (x in 0 until tree.position.x) {
                if (map[tree.position.y][x] >= tree.height) {
                    fromLeft = false
                    break
                }
            }
            if (fromLeft) return true
            var fromRight = true
            for (x in tree.position.x + 1 until map[tree.position.y].size) {
                if (map[tree.position.y][x] >= tree.height) {
                    fromRight = false
                    break
                }
            }
            if (fromRight) return true

            // search column
            var fromTop = true
            for (y in 0 until tree.position.y) {
                if (map[y][tree.position.x] >= tree.height) {
                    fromTop = false
                    break
                }
            }
            if (fromTop) return true
            var fromBottom = true
            for (y in tree.position.y + 1 until map.size) {
                if (map[y][tree.position.x] >= tree.height) {
                    fromBottom = false
                    break
                }
            }
            if (fromBottom) return true
        }
        return false
    }

    fun getScenicScore(map: Array<IntArray>, tree: Tree): Int {
        // outter rows & columns don't see another tree in at least one direction -> 0 scenic value
        if (tree.position.x == 0 || tree.position.y == 0 || tree.position.x == map[0].size - 1 || tree.position.y == map.size - 1) {
            return 0
        }

        //  row
        var toLeft = 0
        for (x in tree.position.x - 1 downTo 0) {
            toLeft++
            if (map[tree.position.y][x] >= tree.height) {
                break
            }
        }
        var toRight = 0
        for (x in tree.position.x + 1 until map[tree.position.y].size) {
            toRight++
            if (map[tree.position.y][x] >= tree.height) {
                break
            }
        }

        //  column
        var toTop = 0
        for (y in tree.position.y - 1 downTo 0) {
            toTop++
            if (map[y][tree.position.x] >= tree.height) {
                break
            }
        }
        var toBottom = 0
        for (y in tree.position.y + 1 until map.size) {
            toBottom++
            if (map[y][tree.position.x] >= tree.height) {
                break
            }
        }
        return toTop * toBottom * toLeft * toRight
    }

    fun part1(input: List<String>): Int {
        val map = input.map { row -> row.map { it.digitToInt() }.toIntArray() }.toTypedArray()
        return getAllTrees(map).filter { isTreeVisible(map, it) }.size
    }

    fun part2(input: List<String>): Int {
        val map = input.map { row -> row.map { it.digitToInt() }.toIntArray() }.toTypedArray()
        return getAllTrees(map).maxOf { getScenicScore(map, it) }
    }

    val testInput = readInput("Day08_test", 2022)
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08", 2022)
    println(part1(input))
    println(part2(input))
}
