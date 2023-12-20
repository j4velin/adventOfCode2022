package aoc2023

import readInput
import java.util.*

object Day20 {

    private enum class PulseType {
        HIGH, LOW;

        fun flip() = if (this == HIGH) LOW else HIGH
    }

    private data class Pulse(val from: Module, val to: Module, val type: PulseType) {
        override fun toString() = "${from.name} --${type}--> ${to.name}"
    }


    private sealed class Module(
        val name: String,
        val incoming: MutableList<Module> = mutableListOf(),
        val outgoing: MutableList<Module> = mutableListOf()
    ) {
        companion object {
            fun parse(input: String): Pair<Module, List<String>> {
                val split = input.split(" -> ")
                val moduleDef = split.first()
                val name = moduleDef.replace("%", "").replace("&", "")
                val module = when {
                    moduleDef == "broadcaster" -> Broadcast(name)
                    moduleDef.first() == '%' -> FlipFlop(name)
                    moduleDef.first() == '&' -> Conjunction(name)
                    else -> throw IllegalArgumentException("Unknown module type: $moduleDef")
                }
                return module to split.last().split(", ")
            }
        }

        abstract fun handlePulse(pulse: Pulse): List<Pulse>

        override fun toString() = "{$name, ${this::class.java.simpleName}, outgoing:${outgoing.map { it.name }}}"
    }

    private class Broadcast(name: String) : Module(name) {
        override fun handlePulse(pulse: Pulse): List<Pulse> =
            outgoing.map { Pulse(this, it, pulse.type) }
    }

    private class FlipFlop(name: String) : Module(name) {
        private var state = PulseType.LOW

        override fun handlePulse(pulse: Pulse): List<Pulse> {
            return if (pulse.type == PulseType.LOW) {
                state = state.flip()
                outgoing.map { Pulse(this, it, state) }
            } else {
                emptyList()
            }
        }
    }

    private class Conjunction(name: String) : Module(name) {
        private val lastStates: Array<PulseType> by lazy { Array(incoming.size) { PulseType.LOW } }

        override fun handlePulse(pulse: Pulse): List<Pulse> {
            val index = incoming.indexOf(pulse.from)
            lastStates[index] = pulse.type

            return if (lastStates.all { it == PulseType.HIGH }) {
                outgoing.map { Pulse(this, it, PulseType.LOW) }
            } else {
                outgoing.map { Pulse(this, it, PulseType.HIGH) }
            }
        }
    }

    private class Sink(name: String) : Module(name) {
        override fun handlePulse(pulse: Pulse) = emptyList<Pulse>()
    }

    fun part1(input: List<String>): Long {
        val modules = input.associate { line ->
            val module = Module.parse(line).first
            module.name to module
        }.toMutableMap()
        input.forEach { line ->
            val parsed = Module.parse(line)
            val current = modules[parsed.first.name]
                ?: throw IllegalArgumentException("No module found with name ${parsed.first.name}")
            current.outgoing.addAll(parsed.second.map {
                var target = modules[it]
                if (target == null) {
                    target = Sink(it).apply { incoming.add(current) }.also { sink -> modules[sink.name] = sink }
                }
                target
            })
            parsed.second.forEach {
                (modules[it] ?: throw IllegalArgumentException("No module found with name $it")).incoming.add(current)
            }
        }

        val start = modules["broadcaster"] ?: throw IllegalArgumentException("No broadcast module found")

        var highSent = 0L
        var lowSent = 0L
        repeat(1000) {
            lowSent++
            val queue: Queue<Pulse> = LinkedList(start.handlePulse(Pulse(start, start, PulseType.LOW)))
            while (queue.isNotEmpty()) {
                val current = queue.poll()
                if (current.type == PulseType.HIGH) {
                    highSent++
                } else {
                    lowSent++
                }
                queue.addAll(current.to.handlePulse(current))
            }
        }

        return highSent * lowSent
    }

    fun part2(input: List<String>): Long {
        return 0L
    }
}

fun main() {
    val testInput = readInput("Day20_test", 2023)
    check(Day20.part1(testInput) == 32000000L)

    val input = readInput("Day20", 2023)
    println(Day20.part1(input))
    println(Day20.part2(input))
}
