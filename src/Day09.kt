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

fun getFreeSpaceSequence(map: List<Int>): Sequence<IntRange> = sequence {
    // The map can't start with free space.
    var start = -1

    for (i in map.indices) {
        val current = map[i]
        val prev = map.getOrNull(i - 1) ?: -2

        if (i == map.lastIndex && current < 0) {
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

fun getFileRightSequence(map: List<Int>): Sequence<IntRange> = sequence {
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

fun getChecksum(map: List<Int>): Long =
    map
        .asSequence()
        .mapIndexed { i, fileIndex -> i.toLong() * fileIndex.coerceAtLeast(0) }
        .sum()

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
    freeSpaceLoop@ for (freeSpace in getFreeSpaceSequence(map)) {
        var remainingFreeSpace = freeSpace
        while (!remainingFreeSpace.isEmpty()) {
            val lastFile = getFileRightSequence(compactMap).firstOrNull()
            if (lastFile == null || lastFile.first < remainingFreeSpace.first) {
                break@freeSpaceLoop
            }
            var written = 0
            remainingFreeSpace.zip(lastFile.reversed()).forEach { (iFreeSpace, iFile) ->
                compactMap[iFreeSpace] = compactMap[iFile]
                compactMap[iFile] = -1
                written++
            }
            remainingFreeSpace = (remainingFreeSpace.first + written)..remainingFreeSpace.last
        }
    }

    val compactDefragMap = map.toMutableList()
    for (file in getFileRightSequence(map)) {
        var suitableFreeSpace: IntRange? = null
        for (freeSpace in getFreeSpaceSequence(compactDefragMap)) {
            if (freeSpace.first > file.first) {
                break
            } else if (freeSpace.count() >= file.count()) {
                suitableFreeSpace = freeSpace
                break
            }
        }
        if (suitableFreeSpace != null) {
            file.zip(suitableFreeSpace).forEach { (iFile, iFreeSpace) ->
                compactDefragMap[iFreeSpace] = compactDefragMap[iFile]
                compactDefragMap[iFile] = -1
            }
        }
    }

    println(getChecksum(compactMap))
    println(getChecksum(compactDefragMap))
}
