package com.adventofcode.problems

import java.io.File
import kotlin.math.pow

var totalWinnings = 0

fun main() {
    val lines: List<String> = File("src/main/resources/input/day4_problem.txt").bufferedReader().readLines()

    // We substring after the colon char, then split the line on the pipe char, and then take that first group of the split
    // and the last group of the split and intersect to find the overlapping values (matches)
    val scores = lines.map { line ->
        line.substringAfter(':').split('|').map {
            it.split(' ').filter(String::isNotEmpty).toSet()
        }.let { it.first().intersect(it.last()).size }
    }

// solve part 1
    println(scores.sumOf { 2.0.pow(it - 1).toInt() })

// solve part 2
    // We create an array of the size of matches found and initialize them all to 1
    val cardsMultiplier = IntArray(scores.size) { 1 }
    scores.forEachIndexed { index, score ->
        (index + 1..index + score).forEach { cardsMultiplier[it] += cardsMultiplier[index] }
    }
    println(cardsMultiplier.sum())

    //doProblem1_day4(lines)

    doProblem2_day4(lines)

    println()
    println()
    println("Total Winnings: $totalWinnings")
}

fun doProblem2_day4(lines: List<String>) {
    val reg = "\\|".toRegex()
    val digitReg = "\\d+".toRegex()
    // How many cards are there? -- Keep a map of card id to value
    var cardCount = 0
    val cardToCountMap = HashMap<Int, Int>()
    val totalCards = lines.size
    for (i in 1..totalCards - 1) {
        cardToCountMap.put(i, 1)
    }
    lines.forEach { line ->
        cardCount++
        val currentCardCount = cardToCountMap[cardCount]
        //println("Current Card Count $currentCardCount")
        val splitCard = line.split(reg)
        val winningNumbers = splitCard[0].split(":")[1]
        val numbersToProcess = digitReg.findAll(splitCard[1]).toList()
        var numbersToProcessList = ArrayList<Int>()
        numbersToProcess.forEach { numberToProcess ->
            numbersToProcessList.add(numberToProcess.value.toInt())
        }

        // Go through all the winning numbers and see if any are in the numbers to process
        var matchCount = 0
        //println("We are on card $cardCount")
        digitReg.findAll(winningNumbers).toList().forEach { winningNumber ->
            val winningNumberInt = winningNumber.value.toInt()
            if (numbersToProcessList.contains(winningNumberInt)) {
                // We've found a match!
                //println("Found a match in $numbersToProcessList [$winningNumberInt]")
                matchCount++ // this is how many cards forward we should add the current card's count to
                val cardOffset = cardCount + matchCount
                if (matchCount == 1) {
                    cardToCountMap[cardOffset] = cardToCountMap.getOrElse(cardOffset, { 0 }) + currentCardCount!!
                    //println("Adding to card $cardOffset , value is now " + cardToCountMap.get(cardOffset))
                } else {
                    if (cardCount + matchCount < totalCards) {
                        cardToCountMap[cardOffset] = cardToCountMap.getOrElse(cardOffset, { 0 }) + currentCardCount!!
                        //println("Adding to card $cardOffset " + cardToCountMap.get(cardOffset))
                    }
                }
            }
        }
    }

    cardToCountMap.forEach { cardNumber, countOfCards ->
        //print("$cardNumber $countOfCards .... ")
        totalWinnings += countOfCards
    }
}

fun doProblem1_day4(lines: List<String>) {
    val reg = "\\|".toRegex()
    val digitReg = "\\d+".toRegex()
    lines.forEach { line ->
        var count = 0
        var cardWinnings = 0
        val splitCard = line.split(reg)
        val winningNumbers = splitCard[0].split(":")[1]
        val numbersToProcess = digitReg.findAll(splitCard[1]).toList()
        println("-------------")
        println("Winning Numbers: ")
        println(winningNumbers)
        println("Numbers Selected: ")
        var numbersToProcessList = ArrayList<Int>()
        numbersToProcess.forEach { numberToProcess ->
            print(numberToProcess.value.toInt())
            print(" ")
            numbersToProcessList.add(numberToProcess.value.toInt())
        }
        digitReg.findAll(winningNumbers).toList().forEach { winningNumber ->
            val winningNumberInt = winningNumber.value.toInt()
            if (numbersToProcessList.contains(winningNumberInt)) {
                if (count == 0) {
                    // First number found, don't double
                    cardWinnings += 1
                    count++
                    println()
                    println("First number $winningNumberInt")
                } else {
                    cardWinnings *= 2
                    count++
                    println()
                    println("Other numbers $winningNumberInt")
                }
            }
        }
        println()
        println("Card winnings: $cardWinnings")
        totalWinnings += cardWinnings
    }
}