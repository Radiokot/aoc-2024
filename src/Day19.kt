fun main() {
    val input = readInput(day = 19, test = false)

    val towels = input[0].split(", ")
    val designs = input.drop(2).filter(String::isNotEmpty)

    val knownCountsByDesign = mutableMapOf<String, Long>()

    fun countWaysFor(design: String): Long {
        if (design in knownCountsByDesign) {
            // I learned memoization.
            return knownCountsByDesign[design]!!
        }

        val toCountFurther = mutableSetOf<String>()
        var exactMatchCount = 0
        towels.forEach { towel ->
            // For each towel, try using it for as the design start.
            // Then, continue counting for what's left,
            // unless the towel matches the design fully.
            if (design.startsWith(towel)) {
                if (design == towel) {
                    exactMatchCount++
                } else {
                    toCountFurther += design.substring(towel.length)
                }
            }
        }

        return (exactMatchCount + toCountFurther.sumOf(::countWaysFor))
            .also { knownCountsByDesign[design] = it }
    }

    var possibleDesignCount = 0L
    var totalWayCount = 0L
    designs.forEach { design ->
        val designWayCount = countWaysFor(design)
        possibleDesignCount += designWayCount.compareTo(0L)
        totalWayCount += designWayCount
    }

    println(possibleDesignCount)
    println(totalWayCount)
}
