package de.proxietv.nisledbot.utils


object DataParserUtil {

    private val UNIT_MILLIS = mapOf(
        *insertValue(31536000000L, "y", "year", "years"),
        *insertValue(2628000000L, "m", "month", "months"),
        *insertValue(604800000L, "w", "week", "weeks"),
        *insertValue(86400000L, "d", "day", "days"),
        *insertValue(3600000L, "h", "hour", "hours"),
        *insertValue(60000L, "min", "minute", "minutes"),
        *insertValue(1000L, "s", "second", "seconds")
    )

    private fun insertValue(value: Long, vararg keys: String): Array<Pair<String, Long>> {
        return keys.map { Pair(it, value) }.toTypedArray()
    }


/*
    private fun parseTime(time: String): Long {
        val time = time.toLowerCase().replace(" ", "")

        val iterator = time.iterator()


        var currentTime = ""
        var currentFormat = ""
        while (iterator.hasNext()) {
            val char = iterator.next()
        }

        var result = 0L
        val finalTime = time
        if (iterator.hasNext()) {
            val unit = iterator.next()
            try {
                time = time.substring(
                    0,
                    time.length - unit.key.length
                ) // Removing the string of the current unit from the time
                var numberStartIndex = 0 // Index where the number for the current timeUnit starts
                for (i in time.length - 1 downTo 0) { // Iterate through chars from the end
                    if (!Character.isDigit(time[i])) { // Checking if char at the index is not numeric
                        numberStartIndex =
                            i + 1 // Adding one to the index, because the current index would be not the start of the number
                        break
                    }
                }
                result += Integer.parseInt(time.substring(numberStartIndex)) * unit.value // Multiplying the found number with the time of the current unit and adding it to the result
                time = time.substring(0, numberStartIndex) //Removing the number from the time
            } catch (ignored: NumberFormatException) {
            }

        }
        return if (result == 0L || time.trim { it <= ' ' }.isEmpty()) result else result + parseTime(time) // Calling the method again for the next unit, when the result is not 0 and the time-String not empty
    }
*/

}