import java.util.*

fun main() {
    val test = false

    val mapWidth = if (test) 11 else 101
    val mapHeight = if (test) 7 else 103
    val map: List<MutableList<MutableSet<Robot>>> = List(mapHeight) {
        MutableList(mapWidth) {
            mutableSetOf()
        }
    }

    fun printMap(
        map: List<List<Set<Robot>>>,
        highContrast: Boolean = false
    ) {
        map.forEach { row ->
            println(row.joinToString("") { robots ->
                if (!highContrast) {
                    if (robots.isEmpty()) "." else robots.size.toString()
                } else {
                    if (robots.isEmpty()) " " else "#"
                }
            })
        }
    }

    val inputLineRegex = "p=(-?\\d+),(-?\\d+)\\sv=(-?\\d+),(-?\\d+)".toRegex()
    readInput(day = 14, test = test)
        .mapNotNull(inputLineRegex::matchEntire)
        .map(MatchResult::destructured)
        .forEachIndexed { i, (startX, startY, velocityX, velocityY) ->
            map[startY.toInt()][startX.toInt()] += Robot(
                index = i,
                velocityX = velocityX.toInt(),
                velocityY = velocityY.toInt(),
            )
        }

//    println("Was:")
//    printMap(map)

    val iterationCount = 10000
    val lowestSafetyIterations = LinkedList<Triple<Int, Int, List<List<Set<Robot>>>>>()

    repeat(iterationCount) { i ->
        val countByQuadrant = IntArray(4)
        val movedRobots = mutableSetOf<Robot>()

        for (y in (0 until mapHeight)) {
            for (x in (0 until mapWidth)) {
                val currentPosition = Position(x, y)
                (map[currentPosition] - movedRobots)
                    .forEach { robot ->
                        val newRobotPosition = Position(
                            x = ((currentPosition.x + robot.velocityX) % mapWidth + mapWidth) % mapWidth,
                            y = ((currentPosition.y + robot.velocityY) % mapHeight + mapHeight) % mapHeight,
                        )
                        map[currentPosition] -= robot
                        map[newRobotPosition] += robot
                        movedRobots += robot

                        if (newRobotPosition.x < mapWidth / 2) {
                            if (newRobotPosition.y < mapHeight / 2) {
                                countByQuadrant[0]++
                            } else if (newRobotPosition.y > mapHeight / 2) {
                                countByQuadrant[2]++
                            }
                        } else if (newRobotPosition.x > mapWidth / 2) {
                            if (newRobotPosition.y < mapHeight / 2) {
                                countByQuadrant[1]++
                            } else if (newRobotPosition.y > mapHeight / 2) {
                                countByQuadrant[3]++
                            }
                        }
                    }
            }
        }

        val safetyFactor = countByQuadrant.reduce { factor, count -> factor * count }
        if (safetyFactor < (lowestSafetyIterations.peek()?.first ?: Int.MAX_VALUE)) {
            val mapCopy = map.map { row ->
                row.map(Set<Robot>::toMutableSet)
            }
            lowestSafetyIterations.push(Triple(safetyFactor, i + 1, mapCopy))
        }

        if (i == iterationCount - 1) {
            println(safetyFactor)
        }
    }

    // Maps with robots gathering close to each other have low safety.
    lowestSafetyIterations
        .take(lowestSafetyIterations.size.coerceAtMost(3))
        .forEach {
            println("Iteration #${it.second} had safety ${it.first}")
            printMap(it.third, highContrast = true)
        }
}

data class Robot(
    val index: Int,
    val velocityX: Int,
    val velocityY: Int,
)
