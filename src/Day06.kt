enum class Direction(val incX: Int, val incY: Int, val marker: Char) {
    N(incX = 0, incY = -1, marker = '^'),
    E(incX = 1, incY = 0, marker = '>'),
    S(incX = 0, incY = 1, marker = 'v'),
    W(incX = -1, incY = 0, marker = '<'),
    ;

    fun turnClockwise() =
        values()[(this.ordinal + 1) % values().size]
}

@JvmInline
value class Position(private val pair: Pair<Int, Int>) {
    constructor(x: Int, y: Int) : this(x to y)

    val x: Int
        get() = pair.first

    val y: Int
        get() = pair.second

    fun step(direction: Direction) =
        Position(
            x = x + direction.incX,
            y = y + direction.incY,
        )
}

class GoingInCirclesException(
    val visits: VisitMap,
) : Exception()

typealias VisitMap = MutableMap<Position, MutableSet<Direction>>

fun patrol(
    field: List<CharArray>,
    startPosition: Position,
    startDirection: Direction,
): VisitMap {
    var position = startPosition
    var direction = startDirection
    val visits: MutableMap<Position, MutableSet<Direction>> = mutableMapOf()

    while (true) {
        if (visits[position]?.contains(direction) == true) {
            throw GoingInCirclesException(visits)
        }

        visits.getOrPut(position, ::mutableSetOf).add(direction)

        val nextStepPosition = position.step(direction)
        val nextStepTile = runCatching { field[nextStepPosition.y][nextStepPosition.x] }.getOrNull()

        when (nextStepTile) {
            '#', 'O' -> {
                direction = direction.turnClockwise()
            }

            null -> {
                break
            }

            else -> {
                position = nextStepPosition
            }
        }
    }

    return visits
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
        readInput("Day06_test")
//        readInput(day = 6)
            .map(String::toCharArray)

    val startPosition = input
        .indexOfFirst { it.contains('^') }
        .let { y -> input[y].indexOf('^') to y }
        .let(::Position)

    // Part 1.
    val visits = patrol(
        field = input,
        startPosition = startPosition,
        startDirection = Direction.N,
    )
    println(visits.size)

    // Part 2.
    val possibleObstacles = mutableSetOf<Position>()
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            if (char == '.') {
                try {
                    line[x] = 'O'
                    patrol(
                        field = input,
                        startPosition = startPosition,
                        startDirection = Direction.N,
                    )
                } catch (target: GoingInCirclesException) {
                    possibleObstacles.add(Position(x, y))
                } finally {
                    line[x] = char
                }
            }
        }
    }
    println(possibleObstacles.size)
}
