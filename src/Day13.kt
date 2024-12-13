import kotlin.math.roundToLong

fun main() {
    val input = readInput(day = 13, test = false)
        .filter(String::isNotEmpty)
        .chunked(3)
        .map { rawMachineSpecs ->
            val (buttonA, buttonB) = rawMachineSpecs.subList(0, 2).map {
                it.substringAfter("X+").substringBefore(",").toLong() to
                        it.substringAfter("Y+").toLong()
            }
            val prize = rawMachineSpecs[2].let {
                it.substringAfter("X=").substringBefore(",").toLong() to
                        it.substringAfter("Y=").toLong()
            }

            MachineSpecs(
                buttonA = buttonA,
                buttonB = buttonB,
                prize = prize,
            )
        }

    /**
     * @return if exists, solution for a1*x + b1*y = c1; a2*x + b2*y = c2
     */
    fun solveLinearSystem(
        a1: Long, b1: Long, c1: Long,
        a2: Long, b2: Long, c2: Long
    ): Pair<Double, Double>? {
        return when (val determinant = a1.toDouble() * b2 - a2.toDouble() * b1) {
            // No unique solution.
            0.0 -> null

            else -> {
                // Apply Cramer's rule.
                val x = (b2 * c1 - b1 * c2) / determinant
                val y = (a1 * c2 - a2 * c1) / determinant

                x to y
            }
        }
    }

    val priceA = 3
    val priceB = 1
    var minTokenCountP1 = 0L
    var minTokenCountP2 = 0L

    input.forEach { machineData ->
        solveLinearSystem(
            a1 = machineData.buttonA.first,
            b1 = machineData.buttonB.first,
            c1 = machineData.prize.first,
            a2 = machineData.buttonA.second,
            b2 = machineData.buttonB.second,
            c2 = machineData.prize.second,
        )?.also { solution ->
            if (solution.first in (0.0..100.0)
                && solution.first % 1 == 0.0
                && solution.second in (0.0..100.0)
                && solution.second % 1 == 0.0
            ) {
                minTokenCountP1 += (priceA * solution.first + priceB * solution.second).roundToLong()
            }
        }

        solveLinearSystem(
            a1 = machineData.buttonA.first,
            b1 = machineData.buttonB.first,
            c1 = 10000000000000 + machineData.prize.first,
            a2 = machineData.buttonA.second,
            b2 = machineData.buttonB.second,
            c2 = 10000000000000 + machineData.prize.second,
        )?.also { solution ->
            if (solution.first > 0.0
                && solution.first % 1 == 0.0
                && solution.second > 0.0
                && solution.second % 1 == 0.0
            ) {
                minTokenCountP2 += (priceA * solution.first + priceB * solution.second).roundToLong()
            }
        }
    }

    println(minTokenCountP1)
    println(minTokenCountP2)
}

data class MachineSpecs(
    val buttonA: Pair<Long, Long>,
    val buttonB: Pair<Long, Long>,
    val prize: Pair<Long, Long>,
)
