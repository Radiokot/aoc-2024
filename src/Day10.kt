fun main() {
    val input = readInput(day = 10, test = false)
        .filter(String::isNotEmpty)
        .map { line ->
            line.map { it.digitToIntOrNull() ?: -1 }
        }

    fun getReachablePeaks(start: Position): Set<Position> {
        val currentHeight = input.getOrNull(start)
            ?: return emptySet()

        if (currentHeight == 9) {
            return setOf(start)
        }

        return sequenceOf(
            Position(start.x + 1, start.y),
            Position(start.x - 1, start.y),
            Position(start.x, start.y + 1),
            Position(start.x, start.y - 1),
        )
            .filter { input.getOrNull(it) == currentHeight + 1 }
            .map(::getReachablePeaks)
            .flatten()
            .toSet()
    }

    var totalScore = 0
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, height ->
            if (height == 0) {
                totalScore += getReachablePeaks(Position(x, y)).size
            }
        }
    }

    println(totalScore)
}
