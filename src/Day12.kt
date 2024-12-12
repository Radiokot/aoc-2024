import Direction.*

fun main() {
    val map = readInput(day = 12, test = false)
        .filter(String::isNotEmpty)
        .map(String::toMutableList)

    fun printMap() {
        map.forEach { row ->
            println(row.joinToString(""))
        }
    }

    fun extractRegion(start: Position): GardenRegion {
        val region = GardenRegion(
            plant = map[start],
        )

        var positionsToCheck = setOf(start)
        while (positionsToCheck.isNotEmpty()) {
            positionsToCheck = buildSet {
                positionsToCheck.forEach { positionToCheck ->
                    if (map.getOrNull(positionToCheck) == region.plant) {
                        region += positionToCheck
                        map[positionToCheck] = '*'

                        addAll(
                            Direction
                                .values()
                                .map(positionToCheck::step)
                        )
                    }
                }
            }
        }

        return region
    }

    val regions = mutableListOf<GardenRegion>()
    map.forEachIndexed { y, row ->
        row.forEachIndexed { x, plant ->
            if (plant.isLetter()) {
                regions += extractRegion(Position(x, y))
            }
        }
    }

    println(regions.sumOf { it.area * it.perimeter })
    println(regions.sumOf { it.area * it.sides })
}

data class GardenRegion(
    val plant: Char,
) {
    var perimeter = 0
    val plots = mutableSetOf<Position>()

    val area: Int
        get() = plots.size

    val sides: Int
        get() = plots.fold(0) { count, position ->
            var sideStartCount = 0

            // Beginning of a left edge.
            if (position.step(W) !in plots
                && (position.step(N) !in plots || position.step(W).step(N) in plots)
            ) {
                sideStartCount++
            }

            // Beginning of a right edge.
            if (position.step(E) !in plots
                && (position.step(N) !in plots || position.step(E).step(N) in plots)
            ) {
                sideStartCount++
            }

            // Beginning of a top edge.
            if (position.step(N) !in plots
                && (position.step(W) !in plots || position.step(N).step(W) in plots)
            ) {
                sideStartCount++
            }

            // Beginning of a bottom edge.
            if (position.step(S) !in plots && (position.step(W) !in plots || position.step(S).step(W) in plots)) {
                sideStartCount++
            }

            count + sideStartCount
        }

    operator fun plusAssign(position: Position) {
        perimeter += 4 - 2 * plots.count { it touches position }
        plots += position
    }
}
