enum class Direction(val incX: Int, val incY: Int, val marker: Char) {
    N(incX = 0, incY = -1, marker = '^'),
    E(incX = 1, incY = 0, marker = '>'),
    S(incX = 0, incY = 1, marker = 'v'),
    W(incX = -1, incY = 0, marker = '<'),
    ;

    fun turnClockwise() =
        values()[(this.ordinal + 1) % values().size]
}

class GoingInCirclesException(
    val visits: VisitMap,
) : Exception()

typealias VisitMap = MutableMap<Position, MutableSet<Direction>>

data class PatrolResult(
    val visits: VisitMap,
    val possibleObstacles: Set<Position>,
)

fun patrol(
    field: List<CharArray>,
    startPosition: Position,
    startDirection: Direction,
    tryObstacles: Boolean = false,
): PatrolResult {
    var position = startPosition
    var direction = startDirection
    val visits: MutableMap<Position, MutableSet<Direction>> = mutableMapOf()
    val possibleObstacles = mutableSetOf<Position>()

    while (true) {
        if (visits[position]?.contains(direction) == true) {
            throw GoingInCirclesException(visits)
        }

        visits.getOrPut(position, ::mutableSetOf).add(direction)

        val nextStepPosition = position.step(direction)
        val nextStepTile = runCatching { field[nextStepPosition.y][nextStepPosition.x] }
            .getOrNull()
            ?: return PatrolResult(visits, possibleObstacles)

        when (nextStepTile) {
            '#', 'O' -> {
                direction = direction.turnClockwise()
            }

            else -> {
                if (tryObstacles) {
                    try {
                        field[nextStepPosition.y][nextStepPosition.x] = 'O'
                        patrol(
                            field = field,
                            startPosition = startPosition,
                            startDirection = startDirection,
                            tryObstacles = false,
                        )
                    } catch (target: GoingInCirclesException) {
                        possibleObstacles.add(nextStepPosition)
                    } finally {
                        field[nextStepPosition.y][nextStepPosition.x] = nextStepTile
                    }
                }

                position = nextStepPosition
            }
        }
    }
}

fun printField(
    field: List<CharArray>,
    visits: VisitMap,
) =
    field.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            print(visits[Position(x, y)]?.lastOrNull()?.marker ?: char)
        }
        println()
    }

fun main() {
    val input =
        readInput(day = 6)
            .map(String::toCharArray)

    val startPosition = input
        .indexOfFirst { it.contains('^') }
        .let { y -> input[y].indexOf('^') to y }
        .let(::Position)

    val (visits, possibleObstacles) = patrol(
        field = input,
        startPosition = startPosition,
        startDirection = Direction.N,
        tryObstacles = true,
    )
    println(visits.size)
    println(possibleObstacles.size)
}
