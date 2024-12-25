fun main() {
    val locks = mutableListOf<List<Int>>()
    val keys = mutableListOf<List<Int>>()

    readInput(day = 25, test = false)
        .chunked(
            size = 8,
            transform = { it.take(7) }
        )
        .forEach { chunk ->
            if (chunk[0].contains('#')) {
                // Lock.
                locks += chunk[0].indices.map { x ->
                    (1 until chunk.size).sumOf { y ->
                        (if (chunk[y][x] == '#') 1 else 0) as Int
                    }
                }
            } else {
                // Key.
                keys += chunk[0].indices.map { x ->
                    (chunk.size - 2 downTo 1).sumOf { y ->
                        (if (chunk[y][x] == '#') 1 else 0) as Int
                    }
                }
            }
        }

    println(
        keys.sumOf { key ->
            locks.count { lock ->
                key.zip(lock, Int::plus).none { it > 5 }
            }
        }
    )
}
