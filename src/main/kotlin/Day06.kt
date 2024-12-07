package org.example

import kotlinx.coroutines.*
import java.io.File

private typealias Map = MutableList<MutableList<Char>>
private typealias Start = Pair<Coordinates, Orientation>

fun main() = runBlocking {
    val originalMap: Map = mutableListOf()
    File("src/main/resources/day-06.txt").readLines().forEach { line ->
        originalMap.add(line.toCharArray().toMutableList())
    }
    val (startPosition, orientation) = findStart(originalMap)

    fun part01() {
        val (_, visited) = walk(originalMap, startPosition, orientation, mutableSetOf())
        println("Distinct positions visited: ${visited.distinctBy { it.first }.size}")
    }

    suspend fun part02() = coroutineScope {
        val (_, visited) = walk(originalMap, startPosition, orientation, mutableSetOf())
        val positionsThatCausesLoop = mutableSetOf<Coordinates>()

        visited
            .filterNot { (coordinates, _) -> coordinates == startPosition }
            .map { visited ->
                launch(Dispatchers.IO) {
                    val newMapWithAdditionalObstruction = cloneMap(originalMap)
                    newMapWithAdditionalObstruction[visited.first.row][visited.first.col] = 'O'

                    runCatching {
                        walk(newMapWithAdditionalObstruction, startPosition, orientation, mutableSetOf())
                    }.onFailure { positionsThatCausesLoop.add(visited.first) }
                }
            }
            .joinAll()

        println("Total positions that causes loop: ${positionsThatCausesLoop.size}")
    }

    part01()
    part02()
}

private fun findStart(map: Map): Start {
    for (row in map.indices) {
        for (col in map[row].indices) {
            if (map[row][col] in Orientation.entries.map { it.char }) {
                return Coordinates(row, col) to Orientation.from(map[row][col])
            }
        }
    }
    throw IllegalArgumentException("Starting position not found")
}

private tailrec fun walk(
    map: Map,
    coordinates: Coordinates,
    orientation: Orientation,
    visited: MutableSet<Pair<Coordinates, Orientation>>
): Pair<Map, MutableSet<Pair<Coordinates, Orientation>>> {
    if (coordinates.isOutOfMap(map))
        return map to visited

    val map = cloneMap(map)
    if (visited.contains(coordinates to orientation)) {
        throw CycleDetectedException()
    }
    map[coordinates.row][coordinates.col] = 'X'
    visited.add(coordinates to orientation)

    return when (orientation) {
        Orientation.FACING_UP -> if (isObstruction(map, coordinates.row - 1, coordinates.col)) {
            walk(map, coordinates, Orientation.FACING_RIGHT, visited)
        } else {
            walk(map, coordinates.toUp(), orientation, visited)
        }

        Orientation.FACING_RIGHT -> if (isObstruction(map, coordinates.row, coordinates.col + 1)) {
            walk(map, coordinates, Orientation.FACING_DOWN, visited)
        } else {
            walk(map, coordinates.toRight(), orientation, visited)
        }

        Orientation.FACING_DOWN -> if (isObstruction(map, coordinates.row + 1, coordinates.col)) {
            walk(map, coordinates, Orientation.FACING_LEFT, visited)
        } else {
            walk(map, coordinates.toDown(), orientation, visited)
        }

        else -> if (isObstruction(map, coordinates.row, coordinates.col - 1)) {
            walk(map, coordinates, Orientation.FACING_UP, visited)
        } else {
            walk(map, coordinates.toLeft(), orientation, visited)
        }
    }
}

private fun Coordinates.isOutOfMap(map: Map): Boolean =
    row < 0 || row > map.lastIndex || col < 0 || col > map[0].lastIndex

private data class Coordinates(val row: Int, val col: Int) {
    fun toUp(): Coordinates = Coordinates(row - 1, col)
    fun toDown(): Coordinates = Coordinates(row + 1, col)
    fun toLeft(): Coordinates = Coordinates(row, col - 1)
    fun toRight(): Coordinates = Coordinates(row, col + 1)
}

private enum class Orientation(val char: Char) {
    FACING_UP('^'),
    FACING_DOWN('v'),
    FACING_LEFT('<'),
    FACING_RIGHT('>');

    companion object {
        fun from(char: Char): Orientation =
            entries.find { it.char == char } ?: throw IllegalArgumentException("Invalid orientation")
    }
}

private fun <T> List<List<T>>.getOrNull(row: Int, col: Int): T? =
    if (row in this.indices && col in this[row].indices) this[row][col] else null

private fun isObstruction(map: Map, row: Int, col: Int): Boolean {
    return map.getOrNull(row, col) in listOf('#', 'O')
}

private fun cloneMap(map: Map): Map {
    val newMap = mutableListOf<MutableList<Char>>()
    for (row in map.indices) {
        newMap.add(map[row].toMutableList())
    }
    return newMap
}

private class CycleDetectedException : RuntimeException("Cycle detected")

