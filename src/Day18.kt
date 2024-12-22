fun main() {
    val WALL = '#'
    val FREE = '.'

    val test = false
    val mapSize = if (test) 7 else 71
    var fallenByteCount = if (test) 12 else 1024

    while (true) {
        val fallenBytes = readInput(day = 18, test = test)
            .take(fallenByteCount)
            .mapTo(mutableListOf()) {
                it.split(',').let { split ->
                    Position(
                        split[0].toInt(),
                        split[1].toInt(),
                    )
                }
            }

        val map: List<List<Char>> = buildList {
            add(List(mapSize + 2) { WALL })

            val fallenBytesSet = fallenBytes.toSet()
            addAll(List(mapSize) { y ->
                listOf(WALL) +
                        List(mapSize) { x ->
                            if (Position(x, y) in fallenBytesSet)
                                WALL
                            else
                                FREE
                        } +
                        listOf(WALL)
            })

            add(this[0])
        }

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

        val startPosition = Position(1, 1)
        val endPosition = Position(mapSize, mapSize)

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

        try {
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
                        val altDistanceToNeighborPosition = distances.getValue(currentPosition) + 1
                        if (altDistanceToNeighborPosition < distances.getOrDefault(neighborPosition, Int.MAX_VALUE)) {
                            distances[neighborPosition] = altDistanceToNeighborPosition
                            previousPositions[neighborPosition] = currentPosition
                        }
                    }

            }

            println("$fallenByteCount: ${distances[endPosition]}")
        } catch (e: NoSuchElementException) {
            println("Can't build a path anymore, last fallen byte is ${fallenBytes.last()}")
            return
        }
        fallenByteCount++
    }
}
