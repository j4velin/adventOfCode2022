package aoc2023

import readInput
import java.util.*

object Day25 {

    @JvmInline
    private value class GroupId(val value: Int)

    private data class Component(val name: String) {
        var groupId: GroupId? = null
        val connections = mutableSetOf<Component>()
    }

    private fun parseComponents(input: List<String>): Map<String, Component> {
        val components = input.flatMap { it.replace(":", "").split(" ") }.map { Component(it) }
            .associateBy { it.name }
        input.forEach { line ->
            val split = line.split(": ")
            val component =
                components[split.first()] ?: throw IllegalArgumentException("Component ${split.first()} not found")
            val connections = split.last().split(" ")
                .map { components[it] ?: throw IllegalArgumentException("Component $it not found") }
            component.connections.addAll(connections)
            connections.forEach { it.connections.add(component) }
        }
        return components
    }

    private tailrec fun findMinCutSets(components: Collection<Component>): Map<GroupId, Collection<Component>> {
        val source = components.filter { it.groupId != null }.random()
        val target = components.filter { it.groupId == null }.randomOrNull()
        return if (target == null) {
            components.groupBy { it.groupId!! }
        } else {
            val distinctPaths = findNumberOfDistinctPaths(source, target)
            if (distinctPaths > 3) {
                target.groupId = source.groupId
            } else {
                target.groupId = GroupId((source.groupId!!.value + 1) % 2)
            }
            findMinCutSets(components)
        }
    }

    private fun findNumberOfDistinctPaths(source: Component, target: Component): Int {
        val alreadyTaken = mutableSetOf<Pair<Component, Component>>()
        var path = findPath(source, target, alreadyTaken)
        var pathsFound = 0
        while (path != null && pathsFound <= 3) {
            pathsFound++
            alreadyTaken.addAll(path)
            path = findPath(source, target, alreadyTaken)
        }
        return pathsFound
    }

    private fun findPath(
        source: Component,
        target: Component,
        alreadyTaken: Set<Pair<Component, Component>>
    ): List<Pair<Component, Component>>? {
        val queue: Queue<Pair<Component, List<Pair<Component, Component>>>> = LinkedList()
        queue.add(source to emptyList())
        val alreadyVisited = mutableSetOf<Component>()
        alreadyVisited.add(source)
        while (queue.isNotEmpty()) {
            val (current, currentPath) = queue.poll()
            alreadyVisited.add(current)
            val connections = current.connections.asSequence()
                .filter { it !in alreadyVisited }
                .filter { (current to it) !in alreadyTaken }
                .filter { (current to it) !in currentPath }
                .filter { (it to current) !in alreadyTaken }
                .filter { (it to current) !in currentPath }
                .toList()
            if (connections.contains(target)) {
                return currentPath + (current to target)
            } else {
                connections.forEach { next -> queue.add(next to (currentPath + (current to next))) }
            }
        }
        return null
    }

    fun part1(input: List<String>): Int {
        val components = parseComponents(input)
        val minConnections = components.values.minOf { it.connections.size }
        return when (minConnections) {
            0, 1, 2 -> throw IllegalArgumentException("Expected assumption not fulfilled: Component with only $minConnections connections found")
            3 -> components.size - 1 // 2 groups: 1 with single component, one with the rest
            else -> {
                components.values.random().groupId = GroupId(1) // assign random element to first group
                val partitions = findMinCutSets(components.values)
                partitions[GroupId(0)]!!.size * partitions[GroupId(1)]!!.size
            }
        }
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}

fun main() {
    val testInput = readInput("Day25_test", 2023)
    check(Day25.part1(testInput) == 54)
    check(Day25.part2(testInput) == 0)

    val input = readInput("Day25", 2023)
    println(Day25.part1(input))
    println(Day25.part2(input))
}
