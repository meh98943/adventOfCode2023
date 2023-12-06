package com.adventofcode.problems

import java.io.File

fun main() {
    val lines: List<String> = File("src/main/resources/input/day6_problem.txt").bufferedReader().readLines()

    //doPart1(lines)
    // Part 2
    val times = lines[0]
    val dist = lines[1]
    val digitReg = "\\d+".toRegex()
    val listOfTimes = digitReg.findAll(times).toList()
    var finalTime = ""
    listOfTimes.forEach { time ->
        finalTime += time.value
    }
    val listOfDists = digitReg.findAll(dist).toList()
    var finalDist = ""
    listOfDists.forEach { dist ->
        finalDist += dist.value
    }

    var winCount = 0
    var msHold = 1
    while (msHold < finalTime.toLong()) {
        // Move one at a time... so timeCount of 1 would hold for 1 ms and then apply 1 to the next 43 entries
        msHold++
        val valueToMultiplyByMsHold = finalTime.toLong() - msHold
        val totalForTry = msHold * valueToMultiplyByMsHold
        if (totalForTry > finalDist.toLong()) {
            winCount++
        }
    }
    println("Win count $winCount")
}

private fun doPart1(lines: List<String>) {
    val times = lines[0]
    val dist = lines[1]
    val digitReg = "\\d+".toRegex()
    val listOfTimes = digitReg.findAll(times).toList()
    val listOfDists = digitReg.findAll(dist).toList()
    var finalWinCount = 1L

    var count = 0
    while (count < listOfTimes.size) {
        var winCount = 0
        val time = listOfTimes[count].value.toLong()
        val dist = listOfDists[count].value.toLong()
        var msHold = 1
        while (msHold < time) {
            // Move one at a time... so timeCount of 1 would hold for 1 ms and then apply 1 to the next 43 entries
            msHold++
            val valueToMultiplyByMsHold = time - msHold
            val totalForTry = msHold * valueToMultiplyByMsHold
            if (totalForTry > dist) {
                winCount++
            }
        }
        println("Win count $winCount for round $count")

        finalWinCount *= winCount
        count++
    }
    println(finalWinCount)
}