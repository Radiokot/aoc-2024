import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() = runBlocking(Dispatchers.Default) {
    val START = 'S'
    val FINISH = 'E'
    val WALL = '#'

    // 20 takes around 30 seconds, 2 – just around 2.
    val maxCheatDistance = 20
    val benefitThreshold = 100
    val map = readInput(day = 20, test = false)
        .filterNot(String::isEmpty)
        .map(String::toMutableList)

    fun printMap(path: Set<Position>? = null) {
        map.forEachIndexed { y, row ->
            println(
                row.mapIndexed { x, cell ->
                    if (path != null && Position(x, y) in path)
                        '◆'
                    else
                        cell
                }.joinToString("")
            )
        }
    }

    val startPosition = map
        .first { it.contains(START) }
        .let { row ->
            Position(
                x = row.indexOf(START),
                y = map.indexOf(row),
            )
        }

    val originalPath = buildList<Position> {
        add(startPosition)

        var previous = startPosition
        while (map[last()] != FINISH) {
            val last = last()
            Direction.values()
                .map(last::move)
                .filter { it != last && it != previous && map[it] != WALL }
                .also(::addAll)
            previous = last
        }
    }
    val originalPathSet = originalPath.toSet()

    fun findCheats(from: Position): Map<Int, MutableSet<String>> {
        val fromIndex = originalPath.indexOf(from)
        val result = mutableMapOf<Int, MutableSet<String>>()
        val toCheck = mutableSetOf(from)
        val visited = mutableSetOf<Position>()

        do {
            val current = toCheck.first()
            toCheck -= current
            visited += current

            if (current in originalPathSet) {
                val distance = current.distanceTo(from)
                val benefit = originalPath.indexOf(current) - fromIndex - distance
                if (benefit > 0) {
                    result.getOrPut(benefit, ::mutableSetOf) += "$from$current"
                }
            }

            Direction.values()
                .map(current::move)
                .filter { it !in visited && it.distanceTo(from) <= maxCheatDistance }
                .also(toCheck::addAll)
        } while (toCheck.isNotEmpty())

        return result
    }

    val cheatsByBenefit = originalPath
        .map { pathPosition ->
            async { findCheats(pathPosition) }
        }
        .awaitAll()
        .fold(mutableMapOf<Int, Int>()) { result, cheatsAtPosition ->
            cheatsAtPosition.forEach { (benefit, cheats) ->
                result[benefit] = (result[benefit] ?: 0) + cheats.size
            }
            result
        }

    println(
        cheatsByBenefit.entries
            .filter { it.key >= benefitThreshold }
            .sumOf { it.value }
    )
}
