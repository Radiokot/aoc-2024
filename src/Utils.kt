import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Reads lines from the given [day] input file.
 */
fun readInput(day: Int) = readInput("Day${day.toString().padStart(2, '0')}")
