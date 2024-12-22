import java.util.*
import kotlin.math.floor

fun main() {
    val seeds = readInput(day = 22, test = false)
        .filter(String::isNotEmpty)
        .map(String::toLong)

    infix fun Long.mix(other: Long): Long =
        this xor other

    fun Long.prune(): Long =
        this % 16777216

    val seedNumbers = seeds.map { seed ->
        var secretNumber = seed
        (1..2000).map {
            secretNumber = (secretNumber mix (secretNumber * 64)).prune()
            secretNumber = (secretNumber mix floor(secretNumber / 32.0).toLong()).prune()
            secretNumber = (secretNumber mix (secretNumber * 2048)).prune()
            secretNumber
        }
    }

    println(seedNumbers.sumOf { it.last() })

    val seedFirstPriceBySequence: List<Map<String, Long>> = List(seeds.size) { seedIndex ->
        buildMap {
            val sequenceBuffer = LinkedList<Long>()
            seedNumbers[seedIndex].forEachIndexed numberForeach@{ numberIndex, number ->
                if (numberIndex == 0) {
                    return@numberForeach
                }

                val currentPrice = number % 10
                val previousPrice = seedNumbers[seedIndex][numberIndex - 1] % 10

                sequenceBuffer += currentPrice - previousPrice

                if (sequenceBuffer.size > 4) {
                    sequenceBuffer.removeFirst()
                } else if (sequenceBuffer.size < 4) {
                    return@numberForeach
                }

                val key = sequenceBuffer.joinToString(",")
                // Because monkey leaves after the first time.
                putIfAbsent(key, currentPrice)
            }
        }
    }

    val bestFuckingDeal = seedFirstPriceBySequence
        .flatMapTo(mutableSetOf(), Map<String, *>::keys)
        .maxOfOrNull { sequence ->
            // Find a sequence which gives the best total price among all the seeds.
            seedFirstPriceBySequence.sumOf { it[sequence] ?: 0L }
        }

    println(bestFuckingDeal)
}
