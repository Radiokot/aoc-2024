import kotlin.io.path.Path
import kotlin.io.path.readText

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

    fun step(direction: Direction) =
        Position(
            x = x + direction.incX,
            y = y + direction.incY,
        )

    operator fun component1(): Int = x

    operator fun component2(): Int = y

    infix fun touches(other: Position): Boolean =
        this.y == other.y && (this.x == other.x + 1 || this.x == other.x - 1) ||
                this.x == other.x && (this.y == other.y + 1 || this.y == other.y - 1)
}

operator fun <T : Any?> List<List<T>>.get(position: Position): T =
    this[position.y][position.x]

fun <T : Any?> List<List<T>>.getOrNull(position: Position): T? =
    this.getOrNull(position.y)?.getOrNull(position.x)

operator fun <T : Any?> List<MutableList<T>>.set(position: Position, value: T): T =
    this[position.y].set(position.x, value)
