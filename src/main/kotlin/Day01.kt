package org.example

import java.io.File
import kotlin.math.abs

fun main() {
    val (list1, list2) = readListsFrom(File("src/main/resources/day-01.txt"))
    list1.sort()
    list2.sort()

    val distances = calculateDistances(list1, list2)
    println("Distances: $distances")
    println("Sum of distances: ${distances.sum()}")

    val similarity = calculateSimilarity(list1, list2)
    println("Similarity: $similarity")
}

private fun calculateDistances(list1: MutableList<Int>, list2: MutableList<Int>): MutableList<Int> {
    val distances = mutableListOf<Int>()
    for (i in 0 until list1.size) {
        distances.add(abs(list2[i] - list1[i]))
    }
    return distances
}

private fun readListsFrom(file: File): Pair<MutableList<Int>, MutableList<Int>> {
    val list1 = mutableListOf<Int>()
    val list2 = mutableListOf<Int>()

    file.useLines { lines ->
        lines.forEach { line ->
            val (num1, num2) = line.split(" ").let { it.first() to it.last() }
            list1.add(num1.toInt())
            list2.add(num2.toInt())
        }
    }
    return list1 to list2
}

private fun calculateSimilarity(list1: MutableList<Int>, list2: MutableList<Int>): Int {
    var similarity = 0

    list1.forEach { num1 ->
        list2.count { num2 -> num1 == num2 }
            .let { totalOccurrences -> similarity += num1 * totalOccurrences }
    }

    return similarity
}