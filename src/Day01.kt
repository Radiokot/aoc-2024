import kotlin.math.abs

fun main() {
    val listA = mutableListOf<Int>()
    val listB = mutableListOf<Int>()

    readInput(day = 1)
        .forEach { line ->
            line.split("   ").also {
                listA.add(it[0].toInt())
                listB.add(it[1].toInt())
            }
        }

    listA.sort()
    listB.sort()

    var totalDifferences = 0
    listA.mapIndexed { i, valueA ->
        val valueB = listB[i]
        totalDifferences += abs(valueA - valueB)
    }

    val mapB = listB.groupBy { it }
    val similarityScore = listA.sumOf { it * (mapB[it]?.size ?: 0) }

    println(totalDifferences)
    println(similarityScore)
}
