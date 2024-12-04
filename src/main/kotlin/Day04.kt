package org.example

import org.example.Direction.*
import java.io.File

fun main() {
    val input = File("src/main/resources/day-04.txt").readLines().map { it.toCharArray().toList() }

    fun part1() {
        val found = input.indices.fold(0) { accRow, row ->
            accRow + input[row].indices.fold(0) { accCol, col ->
                accCol + listOf(
                    input.isXmasWord(row, col, HORIZONTAL_RIGHT),
                    input.isXmasWord(row, col, HORIZONTAL_LEFT),
                    input.isXmasWord(row, col, VERTICAL_UP),
                    input.isXmasWord(row, col, VERTICAL_DOWN),
                    input.isXmasWord(row, col, DIAGONAL_UP_RIGHT),
                    input.isXmasWord(row, col, DIAGONAL_UP_LEFT),
                    input.isXmasWord(row, col, DIAGONAL_DOWN_RIGHT),
                    input.isXmasWord(row, col, DIAGONAL_DOWN_LEFT),
                ).sum()
            }
        }
        println("Found XMAS word: $found")
    }

    fun part2() {
        val found = (1..<input.lastIndex).fold(0) { accRow, row ->
            accRow + (1..<input[row].lastIndex).fold(0) { accCol, col ->
                accCol + if (input.isXmasCross(row, col)) 1 else 0
            }
        }
        println("Sum XMAS cross: $found")
    }

    part1()
    part2()
}

private fun List<List<Char>>.isXmasWord(row: Int, col: Int, direction: Direction): Int {
    val word = "XMAS"
    for (i in word.indices) {
        when (direction) {
            HORIZONTAL_RIGHT -> if (this.getBoundarySafe(row, col + i) != word[i]) return 0
            HORIZONTAL_LEFT -> if (this.getBoundarySafe(row, col - i) != word[i]) return 0
            VERTICAL_UP -> if (this.getBoundarySafe(row - i, col) != word[i]) return 0
            VERTICAL_DOWN -> if (this.getBoundarySafe(row + i, col) != word[i]) return 0
            DIAGONAL_DOWN_RIGHT -> if (this.getBoundarySafe(row + i, col + i) != word[i]) return 0
            DIAGONAL_DOWN_LEFT -> if (this.getBoundarySafe(row + i, col - i) != word[i]) return 0
            DIAGONAL_UP_RIGHT -> if (this.getBoundarySafe(row - i, col + i) != word[i]) return 0
            DIAGONAL_UP_LEFT -> if (this.getBoundarySafe(row - i, col - i) != word[i]) return 0
        }
    }
    return 1
}

private fun List<List<Char>>.isXmasCross(row: Int, col: Int) =
    XmasCrossVariation.entries.any { xmasCrossVariation ->
        this.getBoundarySafe(row, col) == xmasCrossVariation.center
                && this.getBoundarySafe(row - 1, col - 1) == xmasCrossVariation.topLeft
                && this.getBoundarySafe(row - 1, col + 1) == xmasCrossVariation.topRight
                && this.getBoundarySafe(row + 1, col - 1) == xmasCrossVariation.bottomLeft
                && this.getBoundarySafe(row + 1, col + 1) == xmasCrossVariation.bottomRight
    }

enum class Direction {
    HORIZONTAL_RIGHT,
    HORIZONTAL_LEFT,
    VERTICAL_UP,
    VERTICAL_DOWN,
    DIAGONAL_DOWN_RIGHT,
    DIAGONAL_DOWN_LEFT,
    DIAGONAL_UP_RIGHT,
    DIAGONAL_UP_LEFT,
}

enum class XmasCrossVariation(
    val topLeft: Char,
    val topRight: Char,
    val center: Char,
    val bottomLeft: Char,
    val bottomRight: Char
) {
    MSAMS('M', 'S', 'A', 'M', 'S'),
    MMASS('M', 'M', 'A', 'S', 'S'),
    SMASM('S', 'M', 'A', 'S', 'M'),
    SSAMM('S', 'S', 'A', 'M', 'M'),
}

private fun List<List<Char>>.getBoundarySafe(row: Int, col: Int): Char? =
    if (row in this.indices && col in this[row].indices) this[row][col] else null