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

    println(
        """
digraph {
  subgraph x {
    node [style=filled,color=pink];
    ${wireValues.keys.filter { it.startsWith('x') }.sorted().joinToString(separator = " -> ")};
  }
  subgraph y {
    node [style=filled,color=violet];
    ${wireValues.keys.filter { it.startsWith('y') }.sorted().joinToString(separator = " -> ")};
  }
  subgraph and {
    node [style=filled,color=lightgreen];
    ${gates.filter { it.first.second == "AND" }.joinToString(separator = "; ", transform = { it.second })};
  }
  subgraph or {
    node [style=filled,color=yellow];
    ${gates.filter { it.first.second == "OR" }.joinToString(separator = "; ", transform = { it.second })};
  }
  subgraph xor {
    node [style=filled,color=lightskyblue];
    ${gates.filter { it.first.second == "XOR" }.joinToString(separator = "; ", transform = { it.second })};
  }
  subgraph z {
    ${gates.filter { it.second.startsWith('z') }.map { it.second }.sorted().joinToString(separator = " -> ")};
  }
    """
    )

    gates.forEach { gate ->
        println("  ${gate.first.first} -> ${gate.second}; ${gate.first.third} -> ${gate.second};")
    }

    println("}")

    // Paste to https://dreampuf.github.io/GraphvizOnline.
    // Zs must be blue, combine blue and yellow
    // X and Y are connected to a yellow via green
}
