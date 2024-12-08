package org.example

import java.io.File

fun main() {
    val map: MutableList<MutableList<Char>> = mutableListOf()
    File("src/main/resources/day-08.txt").readLines().forEach { line ->
        map.add(line.toCharArray().toMutableList())
    }
    part1(map)
    part2(map)
}

fun part1(map: MutableList<MutableList<Char>>) {
    val map = cloneMap(map)
    val antennas = hashMapOf<Char, MutableList<Position>>()
    val antiNodes = hashSetOf<Position>()

    map.forEachIndexed { i, row ->
        row.forEachIndexed { j, char ->
            if (char.isAntenna()) {
                antennas[char] = antennas.getOrDefault(char, mutableListOf()).apply { add(Position(i, j)) }
            }
        }
    }
    antennas.forEach { (_, positions) ->
        positions.allPairs().forEach { (pos1, pos2) ->
            val (deltaRow, deltaCol) = pos2.row - pos1.row to pos2.col - pos1.col
            antiNodes.addIf(Position(pos1.row - deltaRow, pos1.col - deltaCol)) { it.isNotOutOfBounds(map) }
            antiNodes.addIf(Position(pos2.row + deltaRow, pos2.col + deltaCol)) { it.isNotOutOfBounds(map) }
        }
    }
    println("Total anti-nodes found = ${antiNodes.size}")
}


fun part2(map: MutableList<MutableList<Char>>) {
    val map = cloneMap(map)
    val antennas = hashMapOf<Char, MutableList<Position>>()
    val antiNodes = hashSetOf<Position>()

    map.forEachIndexed { i, row ->
        row.forEachIndexed { j, char ->
            if (char.isAntenna()) {
                antennas[char] = antennas.getOrDefault(char, mutableListOf()).apply { add(Position(i, j)) }
            }
        }
    }
    antennas.forEach { (_, positions) ->
        positions.allPairs().forEach { (pos1, pos2) ->
            val (deltaRow, deltaCol) = pos2.row - pos1.row to pos2.col - pos1.col
            var (antinodeRow1, antinodeCol1) = pos1.row to pos1.col
            while (antinodeRow1 in map.indices && antinodeCol1 in map[0].indices) {
                antiNodes.add(Position(antinodeRow1, antinodeCol1))
                antinodeRow1 -= deltaRow
                antinodeCol1 -= deltaCol
            }

            var (antinodeRow2, antinodeCol2) = pos2.row to pos2.col
            while (antinodeRow2 in map.indices && antinodeCol2 in map[0].indices) {
                antiNodes.add(Position(antinodeRow2, antinodeCol2))
                antinodeRow2 += deltaRow
                antinodeCol2 += deltaCol
            }
        }
    }
    println("Total anti-nodes found = ${antiNodes.size}")
}

private fun Char.isAntenna(): Boolean = this != '.'

private data class Position(val row: Int, val col: Int)

private fun cloneMap(map: MutableList<MutableList<Char>>): MutableList<MutableList<Char>> =
    map.map { it.toMutableList() }.toMutableList()

private fun List<Position>.allPairs(): List<Pair<Position, Position>> {
    val pairs = mutableListOf<Pair<Position, Position>>()
    for (i in indices) {
        for (j in (i + 1..lastIndex)) {
            pairs.add(this[i] to this[j])
        }
    }
    return pairs
}

private fun <T> HashSet<T>.addIf(value: T, predicate: (T) -> Boolean): Boolean =
    if (predicate(value)) add(value) else false

private fun Position.isNotOutOfBounds(map: MutableList<MutableList<Char>>): Boolean =
    this.row in map.indices && this.col in map[0].indices