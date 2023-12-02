package com.adventofcode.problems

import java.io.File

fun main() {
    val lines: List<String> = File("src/main/resources/input/day1_problem1.txt").bufferedReader().readLines()

    val regDigitsOnly = "\\d".toRegex() // for day 1 problem 1
    val reg2 = "(\\d)|(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)|(zero)".toRegex() // for day 1 problem 2
    val reg = "(?=(one|two|three|four|five|six|seven|eight|nine|zero|\\d))".toRegex()
    findNumbers(lines, reg)
}

fun findNumbers(lines: List<String>, reg: Regex) {
    var finalTotal = 0L

    lines.forEach {
        val matchedList = reg.findAll(it).toList()
        if (matchedList.isNotEmpty()) {
            try {
                val number1 = matchedList[0].groups[1]?.let { it1 -> replaceStringNum(it1.value) }
                val number2 = matchedList[matchedList.size - 1].groups[1]?.let { it1 -> replaceStringNum(it1.value) }
                val tempString = number1.toString() + number2.toString()
                println(it)
                println("$number1 $number2")
                finalTotal += tempString.toLong()
            } catch (ex: Exception) {
                //
            }
        }
    }

    println("Total $finalTotal")
}

fun replaceStringNum(value: String): Long {
    val regDigitsOnly = "\\d".toRegex()
    val matchedDigits = regDigitsOnly.findAll(value).toList()
    if (matchedDigits.isNotEmpty()) {
        return value.toLong()
    }

    when (value.lowercase()) {
        "one" -> return 1L
        "two" -> return 2L
        "three" -> return 3L
        "four" -> return 4L
        "five" -> return 5L
        "six" -> return 6L
        "seven" -> return 7L
        "eight" -> return 8L
        "nine" -> return 9L
        "zero" -> return 0L
    }

    throw Exception("Value does not match a number: [$value]")
}