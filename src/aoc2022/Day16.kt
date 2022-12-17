package aoc2022

import readInput
import withEachOf
import java.util.regex.Pattern
import kotlin.math.min

data class Valve(val name: String, val flowRate: Int, val tunnels: Set<String>) {

    companion object {
        fun fromString(input: String): Valve {
            // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
            val regex = Pattern.compile("Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]*)")
            val matcher = regex.matcher(input)
            if (!matcher.matches())
                throw IllegalArgumentException("no valid valve description: '$input'")
            val name = matcher.group(1)
            val flowRate = matcher.group(2).toInt()
            val tunnels = matcher.group(3).split(", ").toSet()
            return Valve(name, flowRate, tunnels)
        }
    }
}

/**
 * @return all reachable [Valve] from [currentValve] in the order in which they contribute the most in regard to their
 * potential to release pressure in the remaining time
 */
private fun getNextValves(currentValve: Valve, caveState: CaveState) =
    caveState.remainingValves.filter { CaveState.distances.contains(currentValve to it) }
        .sortedByDescending { it.flowRate * (caveState.remainingTime - 1 - CaveState.distances[currentValve to it]!!) }

/**
 * Visits (= move to & open) the given [valve]
 *
 * @param valve the [Valve] to visit
 * @param caveState the current state of the cave
 * @param travelTime the time it took to reach [valve]
 * @return the maximum pressure the cave can release by visiting the [valve]
 */
private fun visitValve(valve: Valve, caveState: CaveState, travelTime: Int): Int {
    if (caveState.remainingTime - travelTime <= 1) {
        // no time to open any other valve
        return caveState.pressureRelease
    }

    val newRemainingTime = caveState.remainingTime - 1 - travelTime // time to get here + time to open valve
    val newCaveState = caveState.copy(
        remainingValves = caveState.remainingValves - valve,
        remainingTime = newRemainingTime,
        pressureRelease = caveState.pressureRelease + valve.flowRate * newRemainingTime
    )

    val next = getNextValves(valve, newCaveState)
    return if (next.isEmpty()) {
        newCaveState.pressureRelease
    } else {
        next.maxOf { v -> visitValve(v, newCaveState, CaveState.distances[valve to v]!!) }
    }
}

/**
 * Similar to [visitValve], but can visit 2 [Valve]s in parallel.
 *
 * @param valve1 the first [Valve] to visit
 * @param valve2 the second [Valve] to visit
 * @param caveState the state of the cave
 * @param remainingTravelTime1 the remaining time we need to reach [valve1]
 * @param remainingTravelTime2 the remaining time we need to reach [valve2]
 * @return the maximum pressure the cave can release by visiting [valve1] and [valve2]
 */
private fun visitValves(
    valve1: Valve,
    valve2: Valve,
    caveState: CaveState,
    remainingTravelTime1: Int,
    remainingTravelTime2: Int
): Int {
    if (valve1 == valve2) throw IllegalStateException("Can not visit the same valve twice! $valve1")

    if (caveState.remainingTime - min(remainingTravelTime1, remainingTravelTime2) <= 1) {
        return caveState.pressureRelease
    }

    // arriving at both valves at the same time
    if (remainingTravelTime1 == remainingTravelTime2) {
        val newRemainingTime =
            caveState.remainingTime - 1 - remainingTravelTime1 // time to get here + time to open valve
        val newCaveState = caveState.copy(
            remainingValves = caveState.remainingValves - valve1 - valve2,
            remainingTime = newRemainingTime,
            pressureRelease = caveState.pressureRelease + valve1.flowRate * newRemainingTime + valve2.flowRate * newRemainingTime
        )
        val next1 = getNextValves(valve1, newCaveState)
        val next2 = getNextValves(valve2, newCaveState)
        return if (next1.isEmpty() && next2.isEmpty()) {
            caveState.pressureRelease
        } else if (next1.isEmpty()) {
            next2.maxOf { v ->
                visitValve(v, newCaveState, CaveState.distances[valve2 to v]!!)
            }
        } else if (next2.isEmpty()) {
            next1.maxOf { v ->
                visitValve(v, newCaveState, CaveState.distances[valve1 to v]!!)
            }
        } else if (next1.size == next2.size) {
            if (next1.size == 1) {
                val target = next1.first()
                visitValve(
                    target,
                    newCaveState,
                    min(CaveState.distances[valve1 to target]!!, CaveState.distances[valve2 to target]!!)
                )
            } else {
                next1.asSequence().withEachOf(next2.asSequence()).filter { it.first != it.second }.maxOf { pair ->
                    visitValves(
                        pair.first,
                        pair.second,
                        newCaveState,
                        CaveState.distances[valve1 to pair.first]!!,
                        CaveState.distances[valve2 to pair.second]!!
                    )
                }
            }
        } else {
            // different size
            throw UnsupportedOperationException("too complicated for now")
        }
    } else if (remainingTravelTime1 < remainingTravelTime2) { // arriving at valve1 first
        val newRemainingTime =
            caveState.remainingTime - 1 - remainingTravelTime1 // time to get here + time to open valve
        val newCaveState = caveState.copy(
            remainingValves = caveState.remainingValves - valve1 - valve2, // already en route to valve2
            remainingTime = newRemainingTime,
            pressureRelease = caveState.pressureRelease + valve1.flowRate * newRemainingTime
        )
        val next = getNextValves(valve1, newCaveState)
        return if (next.isEmpty()) {
            visitValve(valve2, newCaveState, remainingTravelTime2 - remainingTravelTime1 - 1)
        } else {
            next.maxOf { v ->
                visitValves(
                    v,
                    valve2,
                    newCaveState,
                    CaveState.distances[valve1 to v]!!,
                    remainingTravelTime2 - remainingTravelTime1 - 1
                )
            }
        }
    } else { // arriving at valve2 first
        val newRemainingTime =
            caveState.remainingTime - 1 - remainingTravelTime2 // time to get here + time to open valve
        val newCaveState = caveState.copy(
            remainingValves = caveState.remainingValves - valve1 - valve2, // already en route to valve1
            remainingTime = newRemainingTime,
            pressureRelease = caveState.pressureRelease + valve2.flowRate * newRemainingTime
        )
        val next = getNextValves(valve2, newCaveState)
        return if (next.isEmpty()) {
            visitValve(valve1, newCaveState, remainingTravelTime1 - remainingTravelTime2 - 1)
        } else {
            next.maxOf { v ->
                visitValves(
                    valve1,
                    v,
                    newCaveState,
                    remainingTravelTime1 - remainingTravelTime2 - 1,
                    CaveState.distances[valve2 to v]!!
                )
            }
        }
    }
}

private data class CaveState(
    val remainingValves: Collection<Valve>,
    val remainingTime: Int,
    val pressureRelease: Int
) {
    companion object {
        lateinit var distances: Map<Pair<Valve, Valve>, Int>
            private set

        /**
         * Sets the [distances] map to the minimum distances between two [Valve]s.
         * @param valves all [Valve] to consider
         * @param valveMapping a mapping from the valve name to the actual [Valve] object
         */
        fun calculateShortestDistances(valves: Collection<Valve>, valveMapping: Map<String, Valve>) {
            val distances = mutableMapOf<Pair<Valve, Valve>, Int>()
            valves.forEach { start ->
                valves.filter { it != start }.forEach { end ->
                    distances[start to end] = findMinDistance(start, end, valveMapping, setOf(start)) ?: Int.MAX_VALUE
                }
            }
            CaveState.distances = distances
        }

        private fun findMinDistance(
            from: Valve,
            to: Valve,
            valveMapping: Map<String, Valve>,
            ignore: Collection<Valve>
        ): Int? {
            return from.tunnels.mapNotNull { valveMapping[it] }.filter { !ignore.contains(it) }.mapNotNull { next ->
                if (to == next) {
                    1
                } else {
                    findMinDistance(next, to, valveMapping, ignore + from)?.inc()
                }
            }.minByOrNull { it }
        }
    }
}

private fun part1(input: List<String>): Int {
    val valves = input.map { Valve.fromString(it) }
    val start = valves.find { it.name == "AA" } ?: throw IllegalArgumentException("No starting valve 'AA' found")
    CaveState.calculateShortestDistances(valves.filter { it.flowRate > 0 } + start, valves.associateBy { it.name })
    val caveState = CaveState(valves.filter { it.flowRate > 0 }, 30, 0)
    return getNextValves(start, caveState).maxOf { v ->
        visitValve(v, caveState, CaveState.distances[start to v]!!)
    }
}

private fun part2(input: List<String>): Int {
    val valves = input.map { Valve.fromString(it) }
    val start = valves.find { it.name == "AA" } ?: throw IllegalArgumentException("No starting valve 'AA' found")
    CaveState.calculateShortestDistances(valves.filter { it.flowRate > 0 } + start, valves.associateBy { it.name })
    val caveState = CaveState(valves.filter { it.flowRate > 0 }, 26, 0)

    val next = getNextValves(start, caveState)
    return next.asSequence().withEachOf(next.asSequence()).filter { it.first != it.second }
        .distinctBy { listOf(it.first.name, it.second.name).sorted() }.map { pair ->
            visitValves(
                pair.first,
                pair.second,
                caveState,
                CaveState.distances[start to pair.first]!!,
                CaveState.distances[start to pair.second]!!
            )
        }.max()
}

fun main() {
    val testInput = readInput("Day16_test", 2022)
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16", 2022)
    println(part1(input))
    println(part2(input)) // takes really long, but seems to work...
}
