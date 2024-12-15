import java.util.*

fun main() {
    val WALL = '#'
    val ROBOT = '@'
    val BOX = 'O'
    val FREE = '.'

    val input = readInput(day = 15, test = false)

    val map: List<MutableList<Char>> = input
        .filter { it.startsWith(WALL) }
        .map(String::toMutableList)

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

    printMap(map)
    println(directions)

    fun moveObject(
        at: Position,
        direction: Direction,
    ) {
        val to = at.step(direction)

        if (map[to] == WALL) {
            error("Can't go into a wall")
        }

        if (map[to] != FREE) {
            moveObject(
                at = to,
                direction = direction
            )
        }

        map[to] = map[at]
        map[at] = FREE
    }

    while (directions.isNotEmpty()) {
        val direction = directions.pop()

        runCatching {
            moveObject(
                at = robotPosition,
                direction = direction,
            )
            robotPosition = robotPosition.step(direction)
        }
    }

    printMap(map)

    println(
        map
            .flatMapIndexed { y, row ->
                row.mapIndexed { x, cell ->
                    if (cell == BOX) {
                        y * 100 + x
                    } else {
                        0
                    }
                }
            }
            .sum()
    )
}
