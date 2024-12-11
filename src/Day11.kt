fun main() {
    val input = readInput(day = 11, test = false)
        .first()

    // After more than 10 iterations stone numbers begin to repeat within a row.
    // Instead of keeping a row of stones in a list, which can't fit in RAM,
    // use a map of counts by number which is quite small.
    var stoneCountMap = input
        .split(" ")
        .map(String::toLong)
        .groupBy { it }
        .mapValues { (_, stones) -> stones.size.toLong() }

    fun change(stoneCountMap: Map<Long, Long>): Map<Long, Long> = buildMap<Long, Long> {
        putAll(stoneCountMap)
        for (stone in stoneCountMap.keys) {
            val previousStoneCount = stoneCountMap.getValue(stone)

            check(stone >= 0)
            check(previousStoneCount >= 0)

            // The current stone is replaced, so decrease the count.
            // Do not remove entirely, as there may be already new stones of the same number.
            put(stone, getValue(stone) - previousStoneCount)

            if (stone == 0L) {
                put(1, getOrDefault(1, 0) + previousStoneCount)
            } else {
                val stoneDigits = mutableListOf<Long>()
                var dividedStone = stone
                while (dividedStone > 0) {
                    stoneDigits += dividedStone % 10
                    dividedStone /= 10
                }

                if (stoneDigits.size % 2 == 0) {
                    stoneDigits
                        .reversed()
                        .chunked(
                            size = stoneDigits.size / 2,
                            transform = { stoneDigitsPart ->
                                stoneDigitsPart.reduce { acc, digit ->
                                    acc * 10 + digit
                                }
                            }
                        )
                        .forEach { newStone ->
                            put(newStone, getOrDefault(newStone, 0) + previousStoneCount)
                        }
                } else {
                    put(stone * 2024, getOrDefault(stone * 2024, 0) + previousStoneCount)
                }
            }
        }
    }.filterValues { it > 0L }

    repeat(25) {
        stoneCountMap = change(stoneCountMap)
    }
    println(stoneCountMap.values.sum())

    repeat(50) {
        stoneCountMap = change(stoneCountMap)
    }
    println(stoneCountMap.values.sum())
}
