fun main() {
    val test = false

    val mapWidth = if (test) 11 else 101
    val mapHeight = if (test) 7 else 103
    val map: List<MutableList<MutableSet<Robot>>> = List(mapHeight) {
        MutableList(mapWidth) {
            mutableSetOf()
        }
    }

    fun printMap(highContrast: Boolean = false) {
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

    println("Was:")
    printMap()

    repeat(10000) {
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
                    }
            }
        }

        if (it > 5000) {
            // High contrast helps to identify the tree
            // in the output using the VS code small scale overview.
            printMap(highContrast = true)
            println(it)
        }
    }


    val countByQuadrant = IntArray(4)
    for (y in (0 until mapHeight)) {
        for (x in (0 until mapWidth)) {
            val currentPosition = Position(x, y)
            val currentCount = map[currentPosition].size
            if (x < mapWidth / 2) {
                if (y < mapHeight / 2) {
                    countByQuadrant[0] += currentCount
                } else if (y > mapHeight / 2) {
                    countByQuadrant[2] += currentCount
                }
            } else if (x > mapWidth / 2) {
                if (y < mapHeight / 2) {
                    countByQuadrant[1] += currentCount
                } else if (y > mapHeight / 2) {
                    countByQuadrant[3] += currentCount
                }
            }
        }
    }
    println(countByQuadrant.reduce { factor, count -> factor * count })
}

data class Robot(
    val index: Int,
    val velocityX: Int,
    val velocityY: Int,
)
