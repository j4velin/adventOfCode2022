package aoc2021

import readInput

private fun String.isLowerCase() = all { it.isLowerCase() }

private class Graph(input: List<String>) {

    private val nodes: Collection<Node>
    private val start: Node
    private val end: Node

    init {
        val nodes = mutableMapOf<Node, Node>()
        input.forEach { line ->
            val newEdge = line.split("-").map { Node(it) }.map { node -> nodes.getOrPut(node) { node } }
            if (newEdge.size != 2) {
                throw IllegalArgumentException("aoc2021.Line $line is not valid")
            }
            newEdge[0].edges.add(newEdge[1])
            newEdge[1].edges.add(newEdge[0])
        }
        this.nodes = nodes.keys
        this.start = this.nodes.find { it.name == "start" } ?: throw IllegalArgumentException("No start node")
        this.end = this.nodes.find { it.name == "end" } ?: throw IllegalArgumentException("No end node")
    }

    data class Node(val name: String) {
        val edges = mutableSetOf<Node>()
    }

    /**
     * Finds all paths from [start] to [end] with the rules given by the part 1 description: No small cave can be
     * visited twice
     * @return all possible paths from [start] to [end]
     */
    fun findPaths() = findPath(start, setOf(start), start)

    /**
     * Finds all paths from [start] to [end] with the rules given by the part 2 description: A single small cave can be
     * visited twice
     * @return all possible paths from [start] to [end]
     */
    fun findPaths2() = findPath(start, setOf(start), null)

    /**
     * Finds the paths from [startNode] to [end]
     * @param startNode         the node at which to start the search
     * @param alreadyVisited    a set of already visited 'small' nodes
     * @param visitedTwice      the small cave, that has been visited twice or null, if no small cave has been visited
     *                          twice so far
     * @return all possible paths from [startNode] to [end]
     */
    private fun findPath(startNode: Node, alreadyVisited: Set<Node>, visitedTwice: Node?): Sequence<List<Node>> =
        sequence {
            for (node in startNode.edges) {
                when (node) {
                    // start & end are never allowed to be visited twice -> always ending the current path
                    start -> continue
                    end -> yield(listOf(startNode, node))
                    else -> {
                        val visitedNodes = alreadyVisited.toMutableSet()
                        val remainingPaths = if (node.name.isLowerCase() && !visitedNodes.add(node)) {
                            // is an already visited small cave
                            if (visitedTwice != null) {
                                emptySequence()
                            } else {
                                findPath(node, visitedNodes, node)
                            }
                        } else {
                            // is a big cave, which can be visited regardless of the content of visitedNodes
                            findPath(node, visitedNodes, visitedTwice)
                        }
                        remainingPaths.forEach {
                            yield(buildList {
                                add(startNode)
                                addAll(it)
                            })
                        }
                    }
                }
            }
        }
}

private fun part1(input: List<String>) = Graph(input).findPaths().count()

private fun part2(input: List<String>) = Graph(input).findPaths2().count()

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 226)
    check(part2(testInput) == 3509)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
