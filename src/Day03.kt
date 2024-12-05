fun main() {
    val input =
        "do()" + readInput(day = 3).joinToString("")

    val multiplicationRegex = "mul\\((\\d+),(\\d+)\\)".toRegex()
    val part1 = multiplicationRegex.findAll(input).sumOf {
        it.groupValues[1].toInt() * it.groupValues[2].toInt()
    }
    println(part1)

    val activation = (
            "do\\(\\)".toRegex().findAll(input).map { it.range.first to true } +
                    "don't\\(\\)".toRegex().findAll(input).map { it.range.first to false }
            ).sortedBy { it.first }
    val part2 = multiplicationRegex.findAll(input).sumOf {
        val index = it.range.first
        val isActive = activation.last { it.first < index }.second
        if (isActive) {
            it.groupValues[1].toInt() * it.groupValues[2].toInt()
        } else {
            0
        }
    }
    println(part2)
}
