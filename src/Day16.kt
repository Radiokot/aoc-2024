import kotlin.math.abs

fun main() {
    val START = 'S'
    val FINISH = 'E'
    val WALL = '#'

    val map = readInput(day = 16, test = false)
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
    val endPosition = map
        .first { it.contains(FINISH) }
        .let { row ->
            Position(
                x = row.indexOf(FINISH),
                y = map.indexOf(row),
            )
        }

    // Solving with Dijkstra.
    val distances = mutableMapOf<Position, Int>()
    distances[startPosition] = 0
    val previousPositions = mutableMapOf<Position, Position>()
    val positions = map
        .mapIndexed { y, row ->
            row.mapIndexedNotNull { x, cell ->
                if (cell != WALL)
                    Position(x, y)
                else
                    null
            }
        }
        .flatten()
        .toMutableSet()

    while (positions.isNotEmpty()) {
        val currentPosition = positions.minBy { distances.getOrDefault(it, Int.MAX_VALUE) }

        positions.remove(currentPosition)

        if (currentPosition == endPosition) {
            break
        }

        Direction.values()
            .map(currentPosition::move)
            .filter { map[it] != WALL }
            .forEach { neighborPosition ->
                val previousToCurrentPosition: Position? = previousPositions[currentPosition]
                val altDistanceToNeighborPosition = distances.getValue(currentPosition) +
                        if (previousToCurrentPosition == null) {
                            // This is when checking distance from the start, facing east.
                            if (neighborPosition.x < currentPosition.x)
                            // Turn back (west).
                                2000 + 1
                            else if (neighborPosition.y != currentPosition.y)
                            // Turn north or south.
                                1000 + 1
                            else
                                1
                        } else {
                            if (abs(neighborPosition.y - previousToCurrentPosition.y) == 1
                                || abs(neighborPosition.x - previousToCurrentPosition.x) == 1
                            )
                            // Turn.
                                1000 + 1
                            else
                                1
                        }
                if (altDistanceToNeighborPosition < distances.getOrDefault(neighborPosition, Int.MAX_VALUE)) {
                    distances[neighborPosition] = altDistanceToNeighborPosition
                    previousPositions[neighborPosition] = currentPosition
                }
            }

    }

    println(distances[endPosition])

    val bestPath = buildSet {
        var last = endPosition
        add(last)
        while (last != startPosition) {
            add(previousPositions.getValue(last))
            last = previousPositions.getValue(last)
        }
    }
    printMap(bestPath)
}
