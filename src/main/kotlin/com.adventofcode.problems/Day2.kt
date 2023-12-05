package com.adventofcode.problems

import java.io.File

const val GREEN = "green"
const val BLUE = "blue"
const val RED = "red"
val reg = "\\d+".toRegex()

var finalCount = 0L
var violatesConstraints = false


fun main() {
    val lines: List<String> = File("src/main/resources/input/day2_problem.txt").bufferedReader().readLines()
    lines.forEach {
        // Parse next game
        //doProblem1_day2(it)

        doProblem2_day2(it)
    }
    println("Final $finalCount")
}

fun doProblem2_day2(line: String) {
    var redMin = 0L
    var blueMin = 0L
    var greenMin = 0L
    val rounds = line.split(":")[1].split(";") // strip off game, then split by game rounds
    rounds.forEach { round ->
        val values = round.split(",")
        values.forEach { value ->
            val count = reg.findAll(value).toList()[0].value.toLong()
            // Figure out the min count for each color in a round
            if (value.contains(RED) && redMin < count) {
                redMin = count
            } else if (value.contains(BLUE) && blueMin < count) {
                blueMin = count
            } else if (value.contains(GREEN) && greenMin < count) {
                greenMin = count
            }
        }
    }
    finalCount += redMin * blueMin * greenMin
}

fun doProblem1_day2(line: String) {
    val blueLimit = 14
    val greenLimit = 13
    val redLimit = 12

    val gameSplit = line.split(":")
    val rounds = gameSplit[1].split(";") // strip off game, then split by game rounds

    rounds.forEach { round ->
        val values = round.split(",")
        values.forEach { value ->
            val count = reg.findAll(value).toList()[0].value.toLong()
            // Figure out if we violate limits in a round, get the color then get the number
            if (value.contains(RED) && count > redLimit) {
                violatesConstraints = true
            } else if (value.contains(BLUE) && count > blueLimit) {
                violatesConstraints = true
            } else if (value.contains(GREEN) && count > greenLimit) {
                violatesConstraints = true
            } else {
                // Invalid entry or we didn't violate any conditions, continue
            }
        }
    }
    if (!violatesConstraints) {
        finalCount += reg.findAll(gameSplit[0]).toList()[0].value.toLong()
    }
}
