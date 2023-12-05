package com.adventofcode.problems

import java.io.File

fun main() {
    val lines: List<String> = File("src/main/resources/input/day5_problem.txt").bufferedReader().readLines()

    val regNewLine = "^\\n".toRegex()
    val reg = "\\d+".toRegex()

    lines.forEach { line ->
        line.split(":")
    }
}