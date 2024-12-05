fun main() {
    val input =
        readInput(day = 5)

    val rulesByPage = mutableMapOf<Int, MutableSet<Int>>()
    val updates = mutableListOf<List<Int>>()

    input.forEach { inputLine ->
        if (inputLine.contains('|')) {
            inputLine.split('|').also {
                rulesByPage.getOrPut(it[0].toInt(), ::mutableSetOf).add(it[1].toInt())
            }
        } else if (inputLine.contains(',')) {
            updates.add(inputLine.split(',').map(String::toInt))
        }
    }

    fun isUpdateCorrect(update: List<Int>): Boolean {
        val indicesByPage = update
            .mapIndexed { index, pageNumber -> pageNumber to index }
            .toMap()

        return update.all { page ->
            rulesByPage[page]
                ?.all { pageRulePage ->
                    val pageIndex = indicesByPage[page]!!
                    (indicesByPage[pageRulePage] ?: Int.MAX_VALUE) > pageIndex
                }
                ?: true
        }
    }

    fun fixUpdate(update: List<Int>): List<Int> =
        update.sortedWith { pageA, pageB ->
            // If pageB is in the rule set for pageA,
            // then pageA goes before pageB (-1),
            // otherwise their order doesn't matter.
            if (rulesByPage.getOrDefault(pageA, emptyList()).contains(pageB)) {
                -1
            } else {
                0
            }
        }

    var part1 = 0
    var part2 = 0
    updates.forEach { update ->
        if (isUpdateCorrect(update)) {
            part1 += update[update.size / 2]
        } else {
            val fixedUpdate = fixUpdate(update)
            part2 += fixedUpdate[fixedUpdate.size / 2]
        }
    }

    println(part1)
    println(part2)
}
