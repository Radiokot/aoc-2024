import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

fun main() {
    val wireValues = mutableMapOf<String, Byte>()
    val gates = mutableSetOf<Pair<Triple<String, String, String>, String>>()

    fun getBinaryValue(variable: Char): String =
        wireValues
            .asSequence()
            .filter { it.key.startsWith(variable) }
            .sortedByDescending { it.key }
            .map { it.value }
            .joinToString("")

    readInput(day = 24, test = false).forEach { line ->
        if (line.contains(":")) {
            line.split(": ").also { (wire, value) ->
                wireValues[wire] = value.toByte()
            }
        } else if (line.contains("->")) {
            line.split(" -> ").also { (operation, destination) ->
                operation.split(" ").also { (a, op, b) ->
                    gates += Triple(a, op, b) to destination
                }
            }
        }
    }

    val computedGates = gates.toMutableSet()
    computedGates.clear()
    do {
        for (gate in (gates - computedGates)) {
            val (a, op, b) = gate.first
            val dest = gate.second

            if (a in wireValues && b in wireValues) {
                wireValues[dest] = when (op) {
                    "AND" -> wireValues[a]!! and wireValues[b]!!
                    "OR" -> wireValues[a]!! or wireValues[b]!!
                    "XOR" -> wireValues[a]!! xor wireValues[b]!!
                    else -> error("Unknown operation $op")
                }
                computedGates += gate
            }
        }
    } while (computedGates.size != gates.size)

    println(
       getBinaryValue('z')
            .toLong(2)
    )
}
