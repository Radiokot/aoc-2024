fun main() {
    val connectionsByComputer = mutableMapOf<String, MutableSet<String>>()

    readInput(day = 23, test = false)
        .filter(String::isNotEmpty)
        .forEach { pair ->
            pair.split("-").let { (a, b) ->
                connectionsByComputer.getOrPut(a, ::mutableSetOf) += b
                connectionsByComputer.getOrPut(b, ::mutableSetOf) += a
            }
        }

    val partiesOf3 = connectionsByComputer.flatMapTo(mutableSetOf()) { (computer, connections) ->
        // For each connected computer, we found which of its connection
        // are present in the current computer connections too (intersection)
        // and for each of those create a "party of 3" entry.
        connections.flatMap { connection ->
            connectionsByComputer.getValue(connection)
                .intersect(connections)
                .map { intersectedConnection ->
                    // To avoid duplication ([a,b,c]=[b,c,a]), sort the computers.
                    sortedSetOf(computer, connection, intersectedConnection)
                }
        }
    }

    val largeParties = connectionsByComputer
        .flatMapTo(mutableSetOf()) { (computer, connections) ->
            connections.mapNotNull { connection ->
                connectionsByComputer.getValue(connection)
                    .intersect(connections)
                    // To detect large parties, we assume all the intersection is in the party
                    // then literally check all the computers in it are interconnected.
                    .let { (it + setOf(computer, connection)).toSortedSet() }
                    .takeIf { presumableLargeParty ->
                        presumableLargeParty.size > 3 && presumableLargeParty.all { computer ->
                            presumableLargeParty.all { anotherComputer ->
                                computer == anotherComputer || anotherComputer in connectionsByComputer[computer]!!
                            }
                        }
                    }
            }
        }

    println(
        partiesOf3.count { party ->
            party.any { computer ->
                computer.startsWith('t')
            }
        }
    )

    println(
        largeParties
            .maxBy(Set<*>::size)
            .joinToString(",")
    )
}
