package org.example

import java.io.File

typealias Update = MutableList<Int>
typealias Rule = Pair<Int, Int>

fun main() {
    val rules = HashSet<Rule>()
    val updates = mutableListOf<Update>()

    File("src/main/resources/day-05.txt").readLines().forEach { line ->
        if ("\\d+\\|\\d+".toRegex().matches(line)) {
            rules.add(line.split("|").map { it.toInt() }.let { it[0] to it[1] })
        }

        if ("[\\d+,]+".toRegex().matches(line)) {
            updates.add(line.split(",").map { it.toInt() }.toMutableList())
        }
    }

    fun part01() {
        val sumValidUpdates = updates
            .filter { update -> update.isValid(rules) }
            .sumOf { update -> update[update.size / 2] }

        println("Sum of middle pages for valid updates = $sumValidUpdates")
    }

    fun part02() {
        val sumInvalidUpdatesAfterMakingValid = updates
            .filter { !it.isValid(rules) }
            .map { it.makeItValid(rules) }
            .sumOf { update -> update[update.size / 2] }

        println("Sum of middle pages for invalid updates after making valid = $sumInvalidUpdatesAfterMakingValid")
    }

    part01()
    part02()
}

private fun Update.isValid(rules: Set<Pair<Int, Int>>): Boolean {
    for (i in this.indices) {
        for (j in (i + 1..this.lastIndex)) {
            if (rules.contains(this[j] to this[i])) return false
        }
    }
    return true
}

private fun Update.makeItValid(rules: Set<Pair<Int, Int>>): Update {
    val updateCopy = this.toMutableList()
    for (i in updateCopy.indices) {
        for (j in (i + 1..updateCopy.lastIndex)) {
            if (rules.contains(updateCopy[j] to updateCopy[i])) {
                val temp = updateCopy[j]
                updateCopy[j] = updateCopy[i]
                updateCopy[i] = temp
            }
        }
    }
    return updateCopy
}