package org.example

import java.io.File

fun main() {
    val text = File("src/main/resources/day-03.txt").readText()
    val allValidMulStrings = parseTextToMulOperations(text)
    val allStringsMultipliedResult = allValidMulStrings.sumOf { multiplyMulString(it) }
    val enabledStringsMultipliedResult = allValidMulStrings
        .filter { it.startsWith(Action.DO.representation) }
        .sumOf { multiplyMulString(it) }

    println("Multiplying all strings = $allStringsMultipliedResult")
    println("Multiplying all enabled strings = $enabledStringsMultipliedResult")
}

private fun multiplyMulString(str: String): Long {
    val (num1, num2) = str.substringBetween('(', ')').split(",").map { it.toLong() }
    return num1 * num2
}

private fun parseTextToMulOperations(text: String): List<String> {
    val operationStack = ArrayDeque<Char>()
    val actionStack = ArrayDeque<Char>()
    var parsingMode = ParsingMode.OPERATION
    var action = Action.DO
    val validMulStrings = mutableListOf<String>()

    for (char in text.toCharArray()) {
        when {
            char == 'd' -> {
                parsingMode = ParsingMode.ACTION
                clearStacks(operationStack, actionStack)
                actionStack.addLast(char)
            }
            char == 'o' -> actionStack.addLastIfTrueOrElse(
                elem = char,
                ifTrue = { parsingMode == ParsingMode.ACTION && actionStack.isLastOneOf('d') },
                orElse = { clearStacks(actionStack) }
            )
            char == 'n' -> actionStack.addLastIfTrueOrElse(
                elem = char,
                ifTrue = { parsingMode == ParsingMode.ACTION && actionStack.isLastOneOf('o') },
                orElse = { clearStacks(actionStack) }
            )
            char == '\'' -> actionStack.addLastIfTrueOrElse(
                elem = char,
                ifTrue = { parsingMode == ParsingMode.ACTION && actionStack.isLastOneOf('n') },
                orElse = { clearStacks(actionStack) }
            )
            char == 't' -> actionStack.addLastIfTrueOrElse(
                elem = char,
                ifTrue = { parsingMode == ParsingMode.ACTION && actionStack.isLastOneOf('\'') },
                orElse = { clearStacks(actionStack) }
            )
            char == 'm' -> {
                parsingMode = ParsingMode.OPERATION
                clearStacks(operationStack, actionStack)
                operationStack.addLast(char)
            }
            char == 'u' -> operationStack.addLastIfTrueOrElse(
                elem = char,
                ifTrue = { parsingMode == ParsingMode.OPERATION && operationStack.isLastOneOf('m') },
                orElse = { clearStacks(operationStack) }
            )
            char == 'l' -> operationStack.addLastIfTrueOrElse(
                elem = char,
                ifTrue = { parsingMode == ParsingMode.OPERATION && operationStack.isLastOneOf('u') },
                orElse = { clearStacks(operationStack) }
            )
            char == '(' -> {
                if (parsingMode == ParsingMode.ACTION && actionStack.isLastOneOf('o', 't')) {
                    actionStack.addLast(char)
                    continue
                } else if (parsingMode == ParsingMode.OPERATION && operationStack.isLastOneOf('l')) {
                    operationStack.addLast(char)
                    continue
                }
                clearStacks(operationStack, actionStack)
            }
            char.isDigit() -> operationStack.addLastIfTrueOrElse(
                elem = char,
                ifTrue = { parsingMode == ParsingMode.OPERATION && (operationStack.isLastADigit() || operationStack.isLastOneOf('(', ',', '-')) },
                orElse = { clearStacks(operationStack) }
            )
            char == ',' -> operationStack.addLastIfTrueOrElse(
                elem = char,
                ifTrue = { parsingMode == ParsingMode.OPERATION && operationStack.isLastADigit() },
                orElse = { clearStacks(operationStack) }
            )
            char == ')' -> {
                if (parsingMode == ParsingMode.ACTION && actionStack.isLastOneOf('(')) {
                    actionStack.addLast(char)
                    action = Action.from(actionStack.joinToString(separator = ""))
                } else if (parsingMode == ParsingMode.OPERATION && operationStack.isLastADigit()) {
                    operationStack.addLast(char)
                    validMulStrings.add(action.representation + operationStack.joinToString(""))
                }
                clearStacks(operationStack, actionStack)
            }
            else -> clearStacks(operationStack, actionStack)
        }
    }

    return validMulStrings
}

enum class ParsingMode { ACTION, OPERATION }

enum class Action(val str: String, val representation: String) {
    DO("do()", "#"),
    DONT("don't()", "!");

    companion object {
        fun from(input: String): Action {
            return entries.first { it.str == input }
        }
    }
}

private fun <T> ArrayDeque<T>.addLastIfTrueOrElse(
    elem: T,
    ifTrue: ArrayDeque<T>.() -> Boolean,
    orElse: ArrayDeque<T>.() -> Any
) {
    if (ifTrue()) {
        addLast(elem)
    } else {
        orElse()
    }
}

private fun <T> clearStacks(vararg stacks: ArrayDeque<T>) {
    stacks.forEach { it.clear() }
}

private fun ArrayDeque<Char>.isLastADigit(): Boolean =
    !isEmpty() && last().isDigit()

private fun <T> ArrayDeque<T>.isLastOneOf(vararg possibilities: T): Boolean =
    !isEmpty() && possibilities.contains(last())

private fun String.substringBetween(start: Char, end: Char): String =
    this.substringAfter(start).substringBefore(end)