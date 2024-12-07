fun canReach(
    target: Long,
    operands: List<Long>,
    operators: Sequence<(Long, Long) -> Long>,
): Boolean =
    target in operands.foldIndexed(setOf(operands[0])) { i, calculations, operand ->
        if (i == 0) {
            calculations
        } else {
            calculations.flatMapTo(hashSetOf()) { calculation ->
                operators
                    .map { operator -> operator(calculation, operand) }
                    .filter { it <= target }
            }
        }
    }

fun main() {
    val input =
        readInput(
            day = 7,
            test = false,
        )
            .filter(String::isNotEmpty)
            .map { line ->
                val (target, operands) = line.split(": ").let {
                    it[0].toLong() to it[1].split(" ").map(String::toLong)
                }
                target to operands
            }

    var part1 = 0L
    val part1Operators = sequenceOf(
        { a: Long, b: Long -> a + b },
        { a: Long, b: Long -> a * b },
    )
    var part2 = 0L
    val part2Operators = part1Operators + { a: Long, b: Long -> "$a$b".toLong() }

    input.forEach { (target, operands) ->
        if (canReach(target, operands, part1Operators)) {
            part1 += target
            part2 += target
        } else if (canReach(target, operands, part2Operators)) {
            part2 += target
        }
    }

    println(part1)
    println(part2)
}
