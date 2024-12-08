package org.example

import java.io.File

fun main() {
    val equations = mutableListOf<Equation>()
    File("src/main/resources/day-07.txt").readLines().forEach { line ->
        val tokens = line.split(' ')
        val testValue = tokens[0].substringBefore(':').toLong()
        val numbers = tokens.subList(1, tokens.size).map { it.toLong() }
        equations.add(Equation(testValue, numbers))
    }

    part1(equations)
    part2(equations)
}

fun part1(equations: List<Equation>) {
    fun isValidEquation(testValue: Long, accumulator: Long, numbers: List<Long>): Boolean {
        if (accumulator > testValue) return false
        if (numbers.isEmpty()) return accumulator == testValue

        return isValidEquation(testValue, accumulator + numbers.first(), numbers.drop(1))
                || isValidEquation(testValue, accumulator.coerceAtLeast(1) * numbers.first(), numbers.drop(1))
    }

    val sumValidEquations = equations
        .filter { equation -> isValidEquation(equation.testValue, 0, equation.numbers) }
        .sumOf { it.testValue }

    println("Sum of valid equations = $sumValidEquations")
}

fun part2(equations: List<Equation>) {
    fun concat(number1: Long, number2: Long): Long = "$number1$number2".toLong()

    fun isValidEquation(testValue: Long, accumulator: Long, numbers: List<Long>): Boolean {
        if (accumulator > testValue) return false
        if (numbers.isEmpty()) return accumulator == testValue

        return isValidEquation(testValue, accumulator + numbers.first(), numbers.drop(1))
                || isValidEquation(testValue, accumulator.coerceAtLeast(1) * numbers.first(), numbers.drop(1))
                || isValidEquation(testValue, concat(accumulator, numbers[0]), numbers.drop(1))
    }

    val sumValidEquations = equations
        .filter { equation -> isValidEquation(equation.testValue, 0, equation.numbers) }
        .sumOf { it.testValue }

    println("Sum of valid equations with concat enabled = $sumValidEquations")
}

data class Equation(val testValue: Long, val numbers: List<Long>)