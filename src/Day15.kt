import java.util.*

fun main() {
    val WALL = '#'
    val ROBOT = '@'
    val BOX = 'O'
    val WIDE_BOX = "[]"
    val FREE = '.'

    val input = readInput(day = 15, test = false)
    val wideMap = true

    val map: List<MutableList<Char>> = input
        .filter { it.startsWith(WALL) }
        .map { row ->
            if (wideMap) {
                row.flatMap { cell ->
                    when (cell) {
                        WALL,
                        FREE -> listOf(cell, cell)

                        BOX -> WIDE_BOX.toList()

                        ROBOT -> listOf(ROBOT, FREE)

                        else -> error("Unknown cell type '$cell'")
                    }
                }.toMutableList()
            } else {
                row.toMutableList()
            }
        }

    fun printMap(map: List<List<Char>>) {
        map.forEach { row ->
            println(row.joinToString(""))
        }
    }

    val directions: LinkedList<Direction> = input
        .filter { row ->
            Direction.values().any { row.startsWith(it.marker) }
        }
        .flatMapTo(LinkedList()) { it.map(Direction.Companion::fromMarker) }

    var robotPosition = map
        .first { it.contains(ROBOT) }
        .let { robotRow ->
            Position(
                y = map.indexOf(robotRow),
                x = robotRow.indexOf(ROBOT),
            )
        }

    println("Initial state:")
    printMap(map)
    println(directions)

    fun addPositionsToMove(
        addTo: MutableSet<Position>,
        from: Position,
        direction: Direction,
    ) {
        if (map[from] == FREE) {
            return
        }

        if (map[from] == WALL) {
            error("Can't push into a wall")
        }

        addTo += from

        var objectStart = from
        var width = 1
        val height = 1

        if (map[from] == WIDE_BOX.last()) {
            objectStart = from.move(dx = -1)
            addTo += objectStart
            width = 2
        } else if (map[from] == WIDE_BOX.first()) {
            addTo += from.move(dx = 1)
            width = 2
        }

        when (direction) {
            Direction.N -> {
                (0 until width).forEach { xOffset ->
                    addPositionsToMove(
                        addTo = addTo,
                        from = objectStart.move(direction).move(dx = xOffset),
                        direction = direction,
                    )
                }
            }

            Direction.S -> {
                (0 until width).forEach { xOffset ->
                    addPositionsToMove(
                        addTo = addTo,
                        from = objectStart.move(dx = xOffset, dy = height),
                        direction = direction,
                    )
                }
            }

            Direction.W -> {
                (0 until height).forEach { yOffset ->
                    addPositionsToMove(
                        addTo = addTo,
                        from = objectStart.move(direction).move(dy = yOffset),
                        direction = direction,
                    )
                }
            }

            Direction.E -> {
                (0 until height).forEach { yOffset ->
                    addPositionsToMove(
                        addTo = addTo,
                        from = objectStart.move(dx = width, dy = yOffset),
                        direction = direction,
                    )
                }
            }
        }
    }

    while (directions.isNotEmpty()) {
        val direction = directions.pop()!!
        val positionsToMove = mutableSetOf<Position>()

//        println("Move ${direction.marker}")

        runCatching {
            addPositionsToMove(
                addTo = positionsToMove,
                from = robotPosition,
                direction = direction
            )

            val positionComparator = when (direction) {
                Direction.N ->
                    compareBy(Position::y)

                Direction.S ->
                    compareBy(Position::y).reversed()

                Direction.W ->
                    compareBy(Position::x)

                Direction.E ->
                    compareBy(Position::x).reversed()
            }

            positionsToMove.sortedWith(positionComparator).forEach { positionToMove ->
                map[positionToMove.move(direction)] = map[positionToMove]
                map[positionToMove] = FREE
            }

            robotPosition = robotPosition.move(direction)
        }

//        printMap(map)
//        println()

        map.forEachIndexed { i, row ->
            row.joinToString("").also {
                check(
                    !it.contains("[[")
                            && !it.contains("]]")
                            && !it.contains(".]")
                            && !it.contains("[.")
                ) {
                    "Box teared apart at row ${i + 1}"
                }
            }
        }
    }

    val coordinateSum = map
        .flatMapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                if (cell == BOX || cell == WIDE_BOX.first()) {
                    y * 100 + x
                } else {
                    0
                }
            }
        }
        .sum()

    println(coordinateSum)
}
