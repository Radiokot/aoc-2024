import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.math.floor

fun main() = runBlocking(Dispatchers.Default) {
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

    val seedFirstPriceBySequence: List<Map<String, Long>> = seedNumbers
        .asFlow()
        .map { particularSeedNumbers ->
            async {
                buildMap {
                    val sequenceBuffer = LinkedList<Long>()
                    particularSeedNumbers.forEachIndexed numberForeach@{ numberIndex, number ->
                        if (numberIndex == 0) {
                            return@numberForeach
                        }

                        val currentPrice = number % 10
                        val previousPrice = particularSeedNumbers[numberIndex - 1] % 10

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
        }
        .toList()
        .awaitAll()

    val bestFuckingDeal = seedFirstPriceBySequence
        .flatMapTo(mutableSetOf(), Map<String, *>::keys)
        .asFlow()
        .map { sequence ->
            async {
                // Find a sequence which gives the best total price among all the seeds.
                seedFirstPriceBySequence.sumOf { it[sequence] ?: 0L }
            }
        }
        .toList()
        .awaitAll()
        .max()

    println(bestFuckingDeal)
}
