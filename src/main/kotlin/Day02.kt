package org.example

import java.io.File

fun main() {
    val reports = readListsFrom(File("src/main/resources/day-02.txt"))
    val safeReports = reports.filter { isReportSafe(it, 0) }
    val safeReportsWithOneFaultyTolerance = reports.filter { isReportSafe(it, 1) }

    println("Total safe reports: ${safeReports.count()}")
    println("Total safe reports with tolerance: ${safeReportsWithOneFaultyTolerance.count()}")
}

private fun readListsFrom(file: File): List<List<Int>> = file.useLines { lines ->
    lines.toList().map { line ->
        line.split(" ").map { it.toInt() }
    }
}

private fun isReportSafe(report: List<Int>, tolerance: Int): Boolean {
    val mode = getMode(report)
    if (mode == Mode.UNKNOWN) return false

    return isReportSafeWithTolerance(report.toMutableList(), mode, tolerance)
}

private fun getMode(report: List<Int>): Mode {
    if (report.size < 4) return Mode.UNKNOWN
    val modes = listOf(
        if (report.first() < report.last()) Mode.INCREASING else Mode.DECREASING,
        if (report.first() < report[1]) Mode.INCREASING else Mode.DECREASING,
        if (report[2] < report[3]) Mode.INCREASING else Mode.DECREASING
    )
    return modes.groupBy { mode -> mode }.maxBy { it.value.size }.key
}

private fun isReportSafeWithTolerance(report: MutableList<Int>, mode: Mode, tolerance: Int): Boolean {
    for (i in (1..report.lastIndex)) {
        if (!isSafeVariation(mode, variation = report[i] - report[i - 1])) {
            if (tolerance == 0) return false
            val report1 = report.toMutableList().apply { removeAt(i) }
            val report2 = report.toMutableList().apply { removeAt(i-1) }

            return isReportSafeWithTolerance(report1, mode, 0) || isReportSafeWithTolerance(report2, mode, 0)
        }
    }
    return true
}

private fun isSafeVariation(mode: Mode, variation: Int): Boolean {
    if (mode == Mode.INCREASING && variation in (1..3)) return true
    if (mode == Mode.DECREASING && variation in (-3..-1)) return true
    return false
}

enum class Mode { INCREASING, DECREASING, UNKNOWN }