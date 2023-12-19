package aoc2023

import multiplyOf
import multiplyOfLong
import readInput
import separateBy

object Day19 {

    private data class Part(val properties: Map<Char, Int>) {

        val ratingSum by lazy { properties.values.sum() }

        companion object {
            // {x=787,m=2655,a=1222,s=2876}
            private val regex = """\{x=(?<x>\d+),m=(?<m>\d+),a=(?<a>\d+),s=(?<s>\d+)\}""".toRegex()

            fun fromString(input: String): Part {
                val matchResult = regex.matchEntire(input)
                if (matchResult != null) {
                    val get: (String) -> Int = { matchResult.groups[it]!!.value.toInt() }
                    return Part(
                        mapOf(
                            'x' to get("x"),
                            'm' to get("m"),
                            'a' to get("a"),
                            's' to get("s"),
                        )
                    )
                } else {
                    throw IllegalArgumentException("Part $input does not match")
                }
            }
        }
    }

    private sealed class Result
    private data class Next(val name: String) : Result()
    private data object NoDecision : Result()
    private data object Accepted : Result()
    private data object Rejected : Result()

    private class Rule(val originalInput: String) {

        private val matchResult by lazy { regex.matchEntire(originalInput) }
        val matched by lazy { matchResult != null }
        val property by lazy { matchResult?.groups?.get("property")?.value?.first() }
        val condition by lazy { matchResult?.groups?.get("condition")?.value?.first() }
        val value by lazy { matchResult?.groups?.get("value")?.value?.toInt() }
        val target by lazy { matchResult?.groups?.get("target")?.value ?: originalInput }

        operator fun invoke(part: Part): Result {
            val result = when (target) {
                "A" -> Accepted
                "R" -> Rejected
                else -> Next(target)
            }
            return if (matchResult != null) {
                val partValue = part.properties[property]
                    ?: throw IllegalArgumentException("invalid part: ${part.properties.entries}")

                when (condition) {
                    '>' -> if (partValue > value!!) result else NoDecision
                    '<' -> if (partValue < value!!) result else NoDecision
                    else -> throw IllegalArgumentException("invalid rule condition: $condition")
                }
            } else {
                result
            }
        }

        companion object {
            // a<2006:qkq
            val regex = """(?<property>[xmas])(?<condition>[<>])(?<value>\d+):(?<target>[a-zAR]+)""".toRegex()
            fun fromString(input: String) = Rule(input)
        }
    }

    private data class Workflow(val name: String, val rules: List<Rule>) {

        val accepted = mutableListOf<Part>()

        companion object {

            // px{a<2006:qkq,m>2090:A,rfg}
            private val regex = """(?<name>[a-z]+)\{(?<rules>([xmas][<>]\d+:[a-zAR]+,?)*[a-zAR]+)\}""".toRegex()

            fun fromString(input: String): Workflow {
                val matchResult = regex.matchEntire(input)
                if (matchResult != null) {
                    val name = matchResult.groups["name"]!!.value
                    val rules = matchResult.groups["rules"]!!.value.split(",").map { Rule.fromString(it) }
                    return Workflow(name = name, rules)
                } else {
                    throw IllegalArgumentException("Workflow $input does not match")
                }
            }
        }

        fun process(part: Part): String? {
            rules.forEach {
                when (val result = it(part)) {
                    NoDecision -> {}
                    Rejected -> return null
                    Accepted -> {
                        accepted.add(part)
                        return null
                    }

                    is Next -> return result.name
                }
            }
            throw IllegalArgumentException("No rule matched in workflow $name")
        }
    }

    fun part1(input: List<String>): Int {
        val splitInput = input.separateBy { it.isEmpty() }
        val workflows = splitInput.first().map { Workflow.fromString(it) }.associateBy { it.name }
        val startWorkflow = workflows["in"] ?: throw IllegalArgumentException("No start workflow found")
        val parts = splitInput.last().map { Part.fromString(it) }

        parts.forEach { part ->
            var result = startWorkflow.process(part)
            while (result != null) {
                val nextWorkflow =
                    workflows[result] ?: throw IllegalArgumentException("No workflow found for name $result")
                result = nextWorkflow.process(part)
            }
        }

        val acceptedParts = workflows.values.flatMap { it.accepted }

        return acceptedParts.sumOf { it.ratingSum }
    }

    fun part2(input: List<String>): Long {
        val workflows = input.takeWhile { it.isNotEmpty() }.map { Workflow.fromString(it) }.associateBy { it.name }
        val startWorkflow = workflows["in"] ?: throw IllegalArgumentException("No start workflow found")

        val allParts = mapOf(
            'x' to 1..4000,
            'm' to 1..4000,
            'a' to 1..4000,
            's' to 1..4000,
        )

        return calculateAcceptedParts(allParts, startWorkflow, workflows)
    }

    private fun calculateAcceptedParts(
        ranges: Map<Char, IntRange>,
        currentWorkflow: Workflow,
        workflows: Map<String, Workflow>
    ): Long {
        val currentRanges = ranges.toMutableMap()
        return currentWorkflow.rules.sumOf { rule ->
            if (!rule.matched) {
                when (rule.target) {
                    "A" -> currentRanges.values.multiplyOfLong { it.count().toLong() }
                    "R" -> 0L
                    else -> calculateAcceptedParts(currentRanges, workflows[rule.target]!!, workflows)
                }
            } else {
                val existingRange = currentRanges[rule.property]!!

                val matchedRange: IntRange
                val unmatchedRange: IntRange

                if (rule.condition == '>') {
                    matchedRange = IntRange(rule.value!! + 1, existingRange.last)
                    unmatchedRange = IntRange(existingRange.first, rule.value!!)
                } else {
                    matchedRange = IntRange(existingRange.first, rule.value!! - 1)
                    unmatchedRange = IntRange(rule.value!!, existingRange.last)
                }

                val newRanges = currentRanges
                    .map { if (it.key == rule.property) it.key to matchedRange else it.key to it.value }.toMap()
                currentRanges[rule.property!!] = unmatchedRange

                when (rule.target) {
                    "A" -> newRanges.values.multiplyOfLong { it.count().toLong() }
                    "R" -> 0
                    else -> calculateAcceptedParts(newRanges, workflows[rule.target]!!, workflows)
                }
            }
        }
    }
}

fun main() {
    val testInput = readInput("Day19_test", 2023)
    check(Day19.part1(testInput) == 19114)
    check(Day19.part2(testInput) == 167409079868000L)

    val input = readInput("Day19", 2023)
    println(Day19.part1(input))
    println(Day19.part2(input))
}
