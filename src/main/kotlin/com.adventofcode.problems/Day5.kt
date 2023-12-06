package com.adventofcode.problems

import java.io.File

private val digitReg = "\\d+".toRegex()
private val almanacs = LinkedHashMap<String, ArrayList<Almanac>>()
private val seedsToSoilName = "seed-to-soil"
private val soilToFertilizerName = "soil-to-fertilizer"
private val fertilizerToWaterName = "fertilizer-to-water"
private val waterToLightName = "water-to-light"
private val lightToTempName = "light-to-temperature"
private val tempToHumidityName = "temperature-to-humidity"
private val humidityToLocationName = "humidity-to-location"
private val listOfNames = listOf(
    seedsToSoilName,
    soilToFertilizerName,
    fertilizerToWaterName,
    waterToLightName,
    lightToTempName,
    tempToHumidityName,
    humidityToLocationName
)

var lowestLocationForSeed = Long.MAX_VALUE
fun main() {
    val lines: List<String> = File("src/main/resources/input/day5_problem.txt").bufferedReader().readLines()

    // Build Lists for each almanac
    val seeds = LinkedHashMap<Long, Long>()
    val regForSection = "^(([a-z]+)-to-([a-z])+) map".toRegex()
    var almanacTopicToUse = ""
    lines.forEach { line ->
        if (line.contains("seeds:")) {
            // These values are all on a single line
            val listOfSeedsMatchResults = digitReg.findAll(line).toList()
            var iterator = listOfSeedsMatchResults.iterator()
            while (iterator.hasNext()) {
                var startValueMatch = iterator.next()
                var rangeMatch = iterator.next()
                seeds[startValueMatch.value.toLong()] = rangeMatch.value.toLong()
            }
            return@forEach
        }

        // Process the other sections
        val sectionHeader = regForSection.findAll(line).toList()
        if (sectionHeader.isNotEmpty()) {
            val sectionName = sectionHeader[0].value
            val sectionNameSplit = sectionName.split(" ")[0]
            if (listOfNames.contains(sectionNameSplit)) {
                almanacs[sectionNameSplit] = ArrayList()
                almanacTopicToUse = sectionNameSplit
            }
            // Now that we have our map to use set, we know all the following lines until the next header should be put in that List of Almanac entries
            return@forEach
        } else if (line.isEmpty()) {
            // Skip this line
            return@forEach
        }

        // Add the entry to the List as an Almanac entry
        val entry = Almanac.toAlmanacEntryFromLine(line)
        almanacs[almanacTopicToUse]?.add(entry)

    }

    // Start with a seed, go into seed to soil almanac, get one level deeper,
    // go back up a level and keep iterating

    // Now we have the almanac built.... let's figure out where the seeds go (seed -> location)
    // Determine what the lowest location number is that corresponds to a seed
    // Let's make this easier.. compare a list of possible source values to find if the seeds exist
    var lowestSoilSource = Long.MAX_VALUE
    var highestSoilSource = Long.MIN_VALUE
    almanacs[seedsToSoilName]?.forEach { almanacEntry ->
        val almanacSource = almanacEntry.source
        if (almanacSource < lowestSoilSource) {
            lowestSoilSource = almanacEntry.source
        }
        val topValue = almanacEntry.range + almanacSource
        if (topValue > highestSoilSource) {
            highestSoilSource = topValue
        }
    }

    val seedRanges = LinkedHashMap<Long, Long>()
    val sortedSeeds = seeds.toSortedMap()
    var priorLow = Long.MAX_VALUE
    var priorHigh = Long.MIN_VALUE
    sortedSeeds.forEach { seedEntry ->
        var low = seedEntry.key
        var high = seedEntry.value + seedEntry.key
        if (high > highestSoilSource) {
            // We cannot go higher than this value to map, cap it
            high = highestSoilSource
        }
        println("Processing seed: " + low + " + " + seedEntry.value + " = " + high)
        // Upon setting a priorLow and priorHigh from seeds, we can then determine if the next range falls within this range
        if (low < priorLow && ((low <= priorHigh && priorLow != Long.MAX_VALUE) || priorLow == Long.MAX_VALUE)) {
            priorLow = low
        }
        if (high > priorHigh && ((low <= priorHigh && priorHigh != Long.MIN_VALUE) || priorHigh == Long.MIN_VALUE)) {
            priorHigh = high
        }
        if (priorHigh == high || priorLow == low) {
            // we changed a value, we are still in the range
        } else if (priorLow <= low && low <= priorHigh && priorLow >= high && high <= priorHigh) {
            // We have a range within this current range, skip
        } else {
            // We are on an entry that does not fall in the range previously defined, start a new entry
            seedRanges[priorLow] = priorHigh
            priorLow = low
            priorHigh = high
        }
    }
    seedRanges.forEach { seed ->
        // look in each list, in top-down order from input, to determine where the seed ends up
        // take the range of the source - source+range
        var currentSeedValue = seed.key
        var priorValueToCompare = currentSeedValue
        //var valueToCompare = currentSeedValue
        //recurseOnAlmanacPt1(listOfNames[0], valueToCompare, priorValueToCompare)
        recurseOnAlmanacPt2(listOfNames[0], seed)

        //println("Finished processing seed $currentSeedValue")
        //currentSeedValue++
        // Note: Any source numbers that aren't mapped correspond to the same destination number.
        // So, seed number 10 corresponds to soil number 10 when there is no destination in soil that 10 maps to the source+range
    }

    println()
    println("Lowest Location for Seed $lowestLocationForSeed")
}

fun recurseOnAlmanacPt2(almanacKey: String, rangeToCompare: Map.Entry<Long, Long>): Long {
    var it = almanacs[almanacKey]?.iterator()
    while (it?.hasNext() == true) {
        println("Starting a new Almanac entry for seed-to-soil!")
        // Iterator over entries in the current almanac
        val almanacEntry = it.next()
        var almanacSource: Long = almanacEntry.source
        var almanacRange: Long = almanacEntry.range
        // Check if the range provided starts or ends within the almanac source range
        val bottomOfAlmanacEntryRange = almanacSource
        val topOfAlmanacEntryRange = almanacSource + almanacRange
        // When we find an almanac entry with overlapping seeds in the range, let's see if we can trace any of those seeds to a location
        if (rangeToCompare.key >= bottomOfAlmanacEntryRange || rangeToCompare.value <= topOfAlmanacEntryRange) {
            // Get the overlapping values to check, if any
            var lowInRange = bottomOfAlmanacEntryRange
            var highInRange = topOfAlmanacEntryRange
            val seedRangeBottom = rangeToCompare.key
            val seedRangeTop = rangeToCompare.value
            if (seedRangeBottom >= lowInRange && seedRangeBottom <= highInRange) {
                lowInRange = seedRangeBottom
            }
            if (seedRangeTop <= highInRange && seedRangeTop >= lowInRange) {
                highInRange = seedRangeTop
            }
            if(lowInRange == bottomOfAlmanacEntryRange && highInRange == topOfAlmanacEntryRange) {
                println("The seeds given do not overlap this almanac entry")
                continue
            }
            var seedCount = lowInRange
            var priorValueToCompareTemp = lowInRange
            val nextAlmanacKey = findNextAlmanac(almanacKey)
            println("There are " + highInRange.minus(lowInRange) + " seeds to process")
            while (seedCount <= highInRange) {
                // We are going through the soil almanac here, and want to proceed with the next one
                val difference = seedCount - almanacSource
                lowestLocationForSeed =
                    recurseOnAlmanacPt1(nextAlmanacKey, almanacEntry.destination + difference, priorValueToCompareTemp)
                seedCount++
                if(seedCount % 10000 == 0L) {
                    println("Seed count processed: $seedCount of $highInRange")
                }
            }

        }
    }
    return lowestLocationForSeed
}

fun recurseOnAlmanacPt1(
    almanacKey: String,
    valueToCompare: Long,
    priorValueToCompare: Long
): Long {
    var priorValueToCompareTemp = priorValueToCompare
    // Look at the almanac entries for the given key
    var it = almanacs[almanacKey]?.iterator()
    while (it?.hasNext() == true) {
        val almanacEntry = it.next()
        var almanacSource: Long = almanacEntry.source
        var almanacRange: Long = almanacEntry.range
        if (valueToCompare >= almanacSource && valueToCompare <= almanacSource + almanacRange) {
            val difference = valueToCompare - almanacSource
            // This is good, set the value to compare next, and proceed to the next map
            val nextAlmanacKey = findNextAlmanac(almanacKey)
            if (nextAlmanacKey == "END") {
                if (valueToCompare != priorValueToCompareTemp) {
                    priorValueToCompareTemp = valueToCompare
                    //println("Value to Compare $valueToCompare")
                    if (almanacEntry.destination + difference < lowestLocationForSeed) {
                        lowestLocationForSeed = almanacEntry.destination + difference
                        return lowestLocationForSeed
                    }
                } else {
                    continue
                }
            }
            // More almanacs to go through until location
            lowestLocationForSeed =
                recurseOnAlmanacPt1(nextAlmanacKey, almanacEntry.destination + difference, priorValueToCompareTemp)
        }
        // Didn't find a range that includes `valueToCompare`
    }

    return lowestLocationForSeed
}

fun findNextAlmanac(currentAlmanacKey: String): String {
    var nextOne = false
    if (currentAlmanacKey == listOfNames[6]) {
        // There are no other almanacs after this
        return "END"
    }
    almanacs.forEach { almanac ->
        if (currentAlmanacKey == almanac.key) {
            nextOne = true
        } else if (nextOne) {
            return almanac.key
        }
    }

    throw Exception("We couldn't find another almanac")
}

class Almanac(
    val destination: Long,
    val source: Long,
    val range: Long
) {
    companion object {
        fun toAlmanacEntryFromLine(line: String): Almanac {
            val listOfNumbers = digitReg.findAll(line).toList()
            if (listOfNumbers.isNotEmpty()) {
                // We have values to process into an almanac
                return Almanac(
                    listOfNumbers[0].value.toLong(),
                    listOfNumbers[1].value.toLong(),
                    listOfNumbers[2].value.toLong()
                )
            }
            throw Exception("There was nothing to process into an Almanac entry")
        }
    }

    override fun toString(): String {
        return "$destination $source $range"
    }
}