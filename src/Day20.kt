fun main() {
    val START = 'S'
    val FINISH = 'E'
    val WALL = '#'

    val maxCheatDistance = 20
    val benefitThreshold = 100
    val map = readInput(day = 20, test = false)
        .filterNot(String::isEmpty)
        .map(String::toMutableList)

    val startPosition = map
        .first { it.contains(START) }
        .let { row ->
            Position(
                x = row.indexOf(START),
                y = map.indexOf(row),
            )
        }

    val originalPathIndices = mutableMapOf<Position, Int>(startPosition to 0)
    buildList<Position> {
        add(startPosition)
        while (map[last()] != FINISH) {
            val next = Direction.values()
                .map(last()::move)
                .first { !originalPathIndices.contains(it) && map[it] != WALL }
            add(next)
            originalPathIndices[next] = lastIndex
        }
    }

    fun findCheats(from: Position): Map<Int, MutableSet<String>> {
        val fromIndex = originalPathIndices.getValue(from)
        val result = mutableMapOf<Int, MutableSet<String>>()

        for (x in (from.x - maxCheatDistance..from.x + maxCheatDistance)) {
            for (y in (from.y - maxCheatDistance..from.y + maxCheatDistance)) {
                val current = Position(x, y)
                val distance = current.distanceTo(from)
                if (originalPathIndices.contains(current) && distance <= maxCheatDistance) {
                    val benefit = originalPathIndices.getValue(current) - fromIndex - distance
                    if (benefit > 0) {
                        result.getOrPut(benefit, ::mutableSetOf) += "$from$current"
                    }
                }
            }
        }

        return result
    }

    val cheatsByBenefit = originalPathIndices.keys
        .map(::findCheats)
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
