fun printMap(map: List<Int>) {
    println(
        map.joinToString(
            separator = "",
            transform = { index ->
                if (index < 0) {
                    "_"
                } else {
                    "|${index}|"
                }
            })
    )
}

fun main() {
    val input = readInput(day = 9, test = false)
        .first()
        .map(Char::digitToInt)

    val map: List<Int> = input.flatMapIndexed { index, length ->
        if (index % 2 == 0) {
            val fileIndex = index / 2
            List(length) { fileIndex }
        } else {
            List(length) { -1 }
        }
    }

    val compactMap = map.toMutableList()
    var compactChecksum = 0L
    for (i in compactMap.indices) {
        val currentIndex = compactMap[i]

        if (currentIndex >= 0) {
            compactChecksum += i * currentIndex.toLong()
            continue
        }

        val lastFileBlockIndex = compactMap.indexOfLast { it >= 0 }
        if (lastFileBlockIndex > i) {
            compactMap[i] = compactMap[lastFileBlockIndex]
            compactMap[lastFileBlockIndex] = -1
            compactChecksum += i * compactMap[i].toLong()
        } else {
            break
        }
    }

    val compactDefragMap = map.toMutableList()
    val filesTailSeq = sequence<IntRange> {
        var end = -1

        for (i in map.indices.reversed()) {
            val current = map[i]
            val prev = map.getOrNull(i + 1) ?: -1

            if (i == 0 && end >= 0) {
                yield(0..end)
            }

            if (current != prev) {
                if (prev >= 0 && end >= 0) {
                    yield((i + 1)..end)
                }
                if (current >= 0) {
                    end = i
                }
            }
        }
    }

    filesTailSeq.forEach { file ->
        val pitsHeadSeq = sequence<IntRange> {
            var start = -1
            for (i in compactDefragMap.indices) {
                val current = compactDefragMap[i]
                val prev = compactDefragMap.getOrNull(i - 1) ?: -2

                if (i == compactDefragMap.lastIndex && current < 0) {
                    yield(start..i)
                }

                if (current != prev) {
                    if (current < 0) {
                        start = i
                    } else if (prev < 0 && start >= 0) {
                        yield(start until i)
                    }
                }
            }
        }
        val suitablePit = pitsHeadSeq.firstOrNull { it.first < file.first && it.count() >= file.count() }
        if (suitablePit != null) {
            file.zip(suitablePit).forEach { (fileI, pitI) ->
                compactDefragMap[pitI] = compactDefragMap[fileI]
                compactDefragMap[fileI]=-1
            }
        }
    }

//    printMap(map)
//    printMap(compactMap)
//    printMap(compactDefragMap)
    println(compactChecksum)
    println(compactDefragMap.mapIndexed{ i, fileIndex -> i.toLong() * fileIndex.coerceAtLeast(0) }.sum())
}
