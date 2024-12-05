import kotlin.math.abs

fun main() {
    fun getReportTrend(report: List<Int>, isDumperActive: Boolean): Int {
        var dumpedIndex = -1
        return report.foldRightIndexed(0) fold@{ index, level, trend ->
            var previousIndex = (index + 1).coerceAtMost(report.lastIndex)
            if (previousIndex == dumpedIndex) {
                previousIndex = dumpedIndex + 1
            }
            val previousLevel = report[previousIndex]
            val trendChange = level.compareTo(previousLevel)
            val isTrendChangeSafe = abs(level - previousLevel) in (1..3)

            if (index == report.lastIndex
                || index == report.lastIndex - 1 && isTrendChangeSafe
                || trendChange == trend && isTrendChangeSafe
            ) {
                return@fold trendChange
            } else {
                if (isDumperActive && dumpedIndex < 0) {
                    dumpedIndex = index
                    return@fold trend
                } else {
                    return@fold 0
                }
            }
        }
    }

    fun countSafeReports(isDumperActive: Boolean): Int =
        readInput(day = 2)
            .map { it.substringBefore(":").split(" ").map(String::toInt) }
            .sumOf { report ->
                val directTrend = getReportTrend(report, isDumperActive)
                // My dumper can't fix the last level,
                // but if we reverse the report it becomes the first one.
                val reversedTrend = getReportTrend(report.reversed(), isDumperActive)
                if (directTrend + reversedTrend != 0) {
                    1
                } else {
                    abs(directTrend)
                }
            }

    println(countSafeReports(isDumperActive = false))
    println(countSafeReports(isDumperActive = true))
}
