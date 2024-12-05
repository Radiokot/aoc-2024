fun main() {
    val input =
        readInput(day = 4)

    val target = "XMAS"
    val lineWidth = input.first().length

    fun goesRight(line: Int, char: Int, target: String): Boolean {
        if (char + target.length > lineWidth) {
            return false
        }

        target.indices.forEach { targetCharIndex ->
            if (input[line][char + targetCharIndex] != target[targetCharIndex]) {
                return false
            }
        }
        
        return true
    }

    fun goesDown(line: Int, char: Int, target: String): Boolean {
        if (line + target.length > input.size) {
            return false
        }

        target.indices.forEach { targetCharIndex ->
            if (input[line + targetCharIndex][char] != target[targetCharIndex]) {
                return false
            }
        }

        return true
    }

    fun goesDiagonallyRight(line: Int, char: Int, target: String): Boolean {
        if (char + target.length > lineWidth || line + target.length > input.size) {
            return false
        }

        target.indices.forEach { targetCharIndex ->
            if (input[line + targetCharIndex][char + targetCharIndex] != target[targetCharIndex]) {
                return false
            }
        }

        return true
    }

    fun goesDiagonallyLeft(line: Int, char: Int, target: String): Boolean {
        if (char - target.length + 1 < 0 || line + target.length > input.size) {
            return false
        }

        target.indices.forEach { targetCharIndex ->
            if (input[line + targetCharIndex][char - targetCharIndex] != target[targetCharIndex]) {
                return false
            }
        }

        return true
    }

    fun goesXmas(line: Int, char: Int): Boolean {
        if (input[line][char] != 'A'
            || line - 1 < 0 || line + 1 >= input.size
            || char - 1 < 0 || char + 1 >= lineWidth
        ) {
            return false
        }

        return (goesDiagonallyRight(line - 1, char - 1, "MAS")
                || goesDiagonallyRight(line - 1, char - 1, "MAS".reversed()))
                &&
                (goesDiagonallyLeft(line - 1, char + 1, "MAS")
                        || goesDiagonallyLeft(line - 1, char + 1, "MAS".reversed()))
    }

    val part1 = input.indices.sumOf { line ->
        (0 until lineWidth).sumOf { char ->
            var score = 0

            if (goesRight(line, char, target)) {
                score++
            } else if (goesRight(line, char, target.reversed())) {
                score++
            }

            if (goesDown(line, char, target)) {
                score++
            } else if (goesDown(line, char, target.reversed())) {
                score++
            }

            if (goesDiagonallyRight(line, char, target)) {
                score++
            } else if (goesDiagonallyRight(line, char, target.reversed())) {
                score++
            }

            if (goesDiagonallyLeft(line, char, target)) {
                score++
            } else if (goesDiagonallyLeft(line, char, target.reversed())) {
                score++
            }

            score
        }
    }
    println(part1)

    val part2 = input.indices.sumOf { line ->
        (0 until lineWidth).sumOf { char ->
            if (goesXmas(line, char)) {
                1L
            } else {
                0L
            }
        }
    }
    println(part2)
}
