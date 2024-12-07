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
