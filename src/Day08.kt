fun printField(
    field: List<String>,
    antinodes: Set<Position>,
) {
    field.forEachIndexed { y, line ->
        line.forEachIndexed { x, frequency ->
            if (frequency == '.' && antinodes.contains(Position(x, y))) {
                print('#')
            } else {
                print(frequency)
            }
        }
        println()
    }
}

fun findAntinodes(
    antennaA: Position,
    antennaB: Position,
    fieldXRange: IntRange,
    fieldYRange: IntRange,
    depthRange: IntRange,
): Set<Position> {
    val dX = antennaB.x - antennaA.x
    val dY = antennaB.y - antennaA.y

    return buildSet {
        depthRange.forEach { currentDepth ->
            val currentDepthAntinodes = listOf(
                Position(
                    x = antennaB.x + dX * currentDepth,
                    y = antennaB.y + dY * currentDepth,
                ),
                Position(
                    x = antennaA.x - dX * currentDepth,
                    y = antennaA.y - dY * currentDepth,
                )
            )
                .filter { (x, y) -> x in fieldXRange && y in fieldYRange }

            if (currentDepthAntinodes.isNotEmpty()) {
                addAll(currentDepthAntinodes)
            } else {
                return this
            }
        }
    }
}

fun main() {
    val input =
        readInput(day = 8, test = false)
            .filter(String::isNotEmpty)

    val antennasByFrequency: Map<Char, MutableList<Position>> = buildMap {
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, frequency ->
                if (frequency != '.') {
                    getOrPut(frequency, ::mutableListOf).add(Position(x, y))
                }
            }
        }
    }

    val part1Antinodes = mutableSetOf<Position>()
    val part2Antinodes = mutableSetOf<Position>()

    antennasByFrequency.values.forEach { antennas ->
        antennas.forEachIndexed { index, antenna ->
            antennas.drop(index + 1).forEach { otherAntenna ->
                part1Antinodes.addAll(
                    findAntinodes(
                        antennaA = antenna,
                        antennaB = otherAntenna,
                        fieldXRange = input.first().indices,
                        fieldYRange = input.indices,
                        depthRange = (1..1),
                    )
                )
                part2Antinodes.addAll(
                    findAntinodes(
                        antennaA = antenna,
                        antennaB = otherAntenna,
                        fieldXRange = input.first().indices,
                        fieldYRange = input.indices,
                        depthRange = (0..Int.MAX_VALUE),
                    )
                )
            }
        }
    }

//    printField(input, part1Antinodes)
    println(part1Antinodes.size)

//    printField(input, part2Antinodes)
    println(part2Antinodes.size)
}
