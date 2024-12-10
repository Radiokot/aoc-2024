fun main() {
    val input = readInput(day = 10, test = false)
        .filter(String::isNotEmpty)
        .map { line ->
            line.map { it.digitToIntOrNull() ?: -1 }
        }

    fun getReachablePeaks(start: Position): List<Position> {
        val currentHeight = input.getOrNull(start)
            ?: return emptyList()

        if (currentHeight == 9) {
            return listOf(start)
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
            .toList()
    }

    var totalScore = 0
    var totalRating = 0
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, height ->
            if (height == 0) {
                val reachablePicks = getReachablePeaks(Position(x, y))
                totalScore += reachablePicks.distinct().size
                totalRating += reachablePicks.size
            }
        }
    }

    println(totalScore)
    println(totalRating)
}
