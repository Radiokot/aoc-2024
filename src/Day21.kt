import kotlin.math.abs

fun main() {
    val numericKeypad = listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf(' ', '0', 'A'),
    )
    val directionalKeypad = listOf(
        listOf(' ', '^', 'A'),
        listOf('<', 'v', '>'),
    )

    fun getDirectionalInputFor(
        input: List<Char>,
        keypad: List<List<Char>>,
        startPosition: Position,
        avoidGap: Boolean,
    ): List<Char> = buildList {
        var currentPosition = startPosition
        for (i in input.indices) {
            val keyPosition = keypad.positionOf(input[i])
            val dx = keyPosition.x - currentPosition.x
            val dy = keyPosition.y - currentPosition.y

            val directions = buildList {
                addAll(List(abs(dy)) { if (dy > 0) 'v' else '^' })
                addAll(List(abs(dx)) { if (dx > 0) '>' else '<' })
            }.shuffled()

            if (avoidGap) {
                var gapCheckPosition = currentPosition
                val gapPosition = keypad.positionOf(' ')
                directions.forEach { dir ->
                    gapCheckPosition = gapCheckPosition.move(Direction.fromMarker(dir))
                    if (gapCheckPosition == gapPosition) {
                        error("Aimed at the gap!!1!")
                    }
                }
            }
            addAll(directions)

            add('A')

            currentPosition = keyPosition
        }
    }

    var minSum = Int.MAX_VALUE
    val codes = readInput(day = 21, test = false)
        .filter(String::isNotEmpty)
        .map(String::toList)

    val repeatCount = 300000000
    repeat(repeatCount) {
        if (it % 100000 == 0) {
            println(100.0 * it / repeatCount)
        }
        runCatching {
            val complexitySum = codes.sumOf { code ->
                val directionalInput1 =
                    getDirectionalInputFor(
                        input = code,
                        keypad = numericKeypad,
                        startPosition = numericKeypad.positionOf('A'),
                        avoidGap = true,
                    )
                val directionalInput2 =
                    getDirectionalInputFor(
                        input = directionalInput1,
                        keypad = directionalKeypad,
                        startPosition = directionalKeypad.positionOf('A'),
                        avoidGap = true,
                    )
                val directionalInput3 =
                    getDirectionalInputFor(
                        input = directionalInput2,
                        keypad = directionalKeypad,
                        startPosition = directionalKeypad.positionOf('A'),
                        avoidGap = false,
                    )
//            println("${code.joinToString("")}: ${directionalInput1.joinToString("")}")
//            println("${code.joinToString("")}: ${directionalInput2.joinToString("")}")
//            println("${code.joinToString("")}: ${directionalInput3.joinToString("")}")
//            directionalInput3.size.also { print(it) } * code.dropLast(1).joinToString("").toInt()
//                .also { println("x$it") }
                directionalInput3.size * code.dropLast(1).joinToString("").toInt()
            }

            minSum = complexitySum.coerceAtMost(minSum)
        }
    }
    println(minSum)
}
