package com.adventofcode.problems

import java.io.File

var finalTotal = 0L
var number1Found = 0
var number2Found = 0
var number3Found = false

fun main() {
    val lines: List<String> = File("src/main/resources/input/day3_problem.txt").bufferedReader().readLines()

    //doProblem1_day3(lines)
    doProblem2_day3(lines)
    //25,311,067 not correct
    //19,062,549

    println("Final Total $finalTotal")
}

fun doProblem2_day3(lines: List<String>) {
    var i = 0
    val reg = "\\*".toRegex()
    val regDigits = "\\d+".toRegex()
    lines.forEach { line ->
        val starMatches = reg.findAll(line).toList()
        if (starMatches.isEmpty()) {
            return@forEach
        }
        var starPositions = ArrayList<Int>()
        starMatches.forEach { star ->
            starPositions.add(star.range.first)
        }
        var numberMatchesLineAbove = ArrayList<MatchResult>()
        if (i > 0) {
            numberMatchesLineAbove = regDigits.findAll(lines[i - 1]).toList() as ArrayList<MatchResult>
            println("Line above   " + lines[i - 1])
        }
        val numberMatchesSameLine = regDigits.findAll(line).toList()
        println("Current line $line")
        var numberMatchesLineBelow = ArrayList<MatchResult>()
        if (i < lines.size - 1) {
            numberMatchesLineBelow = regDigits.findAll(lines[i + 1]).toList() as ArrayList<MatchResult>
            println("Line below   " + lines[i + 1])
        }

        starPositions.forEach { starPos ->
            println("Star position $starPos")
            numberMatchesSameLine.forEach { number ->
                if (findAdjacentNumbers(starPos, number)) {
                    println("Returning from same line, found too many")
                    return@forEach
                }
            }
            numberMatchesLineAbove.forEach { number ->
                if (findAdjacentNumbers(starPos, number)) {
                    println("Returning from above, found too many")
                    return@forEach
                }
            }
            numberMatchesLineBelow.forEach { number ->
                if (findAdjacentNumbers(starPos, number)) {
                    println("Returning from below, found too many")
                    return@forEach
                }
            }

            if (!number3Found && number1Found > 0 && number2Found > 0) {
                println("New gear $number1Found $number2Found")
                finalTotal += number1Found * number2Found
            }
            // Reset
            number1Found = 0
            number2Found = 0
            number3Found = false
        }
        i++
    }

}

// Note: Returns true if we find more than 2 adjacent numbers
fun findAdjacentNumbers(pos: Int, number: MatchResult): Boolean {
    val firstPos = number.range.first
    val lastPos = number.range.last
    val numValue = number.value.toInt()

    if (pos >= firstPos - 1 && pos <= lastPos + 1) {
        if (number1Found == 0) {
            number1Found = numValue
        } else if (number2Found == 0) {
            number2Found = numValue
        } else {
            // Whoops too many
            number3Found = true
            return true
        }
    }

    return false
}

fun doProblem1_day3(lines: List<String>) {
    val reg = "\\d+".toRegex()
    val regForSymbols = "[^.\\d]".toRegex()
    var i = 0
    lines.iterator().forEach { line ->
        var j = 0
        val foundNumbers = reg.findAll(line).toList()
        foundNumbers.forEach { numberMatch ->
            val numValue = numberMatch.value
            val firstPos = numberMatch.range.first
            val lastPos = numberMatch.range.last

            // Look to the left, right, up, and down (including diagonal)
            val sameLineMatches = regForSymbols.findAll(line).toList()
            if (sameLineMatches.isNotEmpty()) {
                sameLineMatches.forEach { match ->
                    lookForSymbol(match, firstPos, lastPos, numValue)
                }
            }
            if (i != 0) {
                val lineAboveMatches = regForSymbols.findAll(lines[i - 1]).toList()
                if (lineAboveMatches.isNotEmpty()) {
                    lineAboveMatches.forEach { match ->
                        lookForSymbol(match, firstPos, lastPos, numValue)
                    }
                }
            }
            if (i < lines.size - 1) {
                val lineBelowMatches = regForSymbols.findAll(lines[i + 1]).toList()
                if (lineBelowMatches.isNotEmpty()) {
                    lineBelowMatches.forEach { match ->
                        lookForSymbol(match, firstPos, lastPos, numValue)
                    }
                }
            }
        }

        i++
    }
}

fun validatePosition(matchPosition: Int, valueToCompare: Int) {
    if (valueToCompare != matchPosition) {
        println("Symbol length range is > 1: $matchPosition")
    }
}

fun lookForSymbol(match: MatchResult, firstPos: Int, lastPos: Int, numValue: String) {
    val matchPosition = match.range.first
    validatePosition(matchPosition, match.range.last)
    if (matchPosition >= firstPos - 1 && matchPosition <= lastPos + 1) {
        finalTotal += numValue.toLong()
    }
}

fun extra_2DArray(lines: List<String>) {
    val charCountPerLine = lines[0].length

    var grid = Array(lines.size) { Array(charCountPerLine) { ' ' } }
    var i = 0
    lines.forEach { line ->
        var j = 0
        line.forEach { c ->
            grid[i][j] = c
            j++
        }
        println("Done processing line")
        println("$line")
        i++
    }
}