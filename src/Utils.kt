import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.abs

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Reads lines from the given [day] input file.
 */
fun readInput(
    day: Int,
    test: Boolean = false,
) = readInput(
    name = "Day${day.toString().padStart(2, '0')}" + (if (test) "_test" else "")
)

@JvmInline
value class Position(private val pair: Pair<Int, Int>) {
    constructor(x: Int, y: Int) : this(x to y)

    val x: Int
        get() = pair.first

    val y: Int
        get() = pair.second

    fun move(direction: Direction) =
        move(
            dx = direction.incX,
            dy = direction.incY,
        )

    fun move(dx: Int = 0, dy: Int = 0) =
        Position(
            x = x + dx,
            y = y + dy,
        )

    fun distanceTo(other: Position): Int =
        abs(other.x - this.x) + abs(other.y - this.y)

    operator fun component1(): Int = x

    operator fun component2(): Int = y

    infix fun touches(other: Position): Boolean =
        this.y == other.y && (this.x == other.x + 1 || this.x == other.x - 1) ||
                this.x == other.x && (this.y == other.y + 1 || this.y == other.y - 1)

    override fun toString(): String =
        "{$x, $y}"
}

operator fun <T : Any?> List<List<T>>.get(position: Position): T =
    this[position.y][position.x]

fun <T : Any?> List<List<T>>.getOrNull(position: Position): T? =
    this.getOrNull(position.y)?.getOrNull(position.x)

operator fun <T : Any?> List<MutableList<T>>.set(position: Position, value: T): T =
    this[position.y].set(position.x, value)

fun <T : Any?> List<List<T>>.positionOf(element: T): Position =
    first { it.contains(element) }
        .let { row ->
            Position(
                x = row.indexOf(element),
                y = indexOf(row),
            )
        }
