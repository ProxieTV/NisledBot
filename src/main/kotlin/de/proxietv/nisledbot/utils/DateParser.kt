package de.proxietv.nisledbot.utils

class DateParser(private val input: String) {

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


    fun parse(): Long {
        var finalTimeMillis = 0L

        var currentTime = StringBuilder()
        var currentUnit = StringBuilder()

        input.replace(" ", "").toLowerCase().forEach {
            if (Character.isLetter(it) && currentTime.isNotEmpty()) currentUnit = currentUnit.append(it)
            else if (Character.isDigit(it) && currentUnit.isEmpty()) currentTime = currentTime.append(it)
            else {
                finalTimeMillis += currentTime.toString().toLong() * (UNIT_MILLIS[currentUnit.toString()] ?: 0)
                currentTime = currentTime.clear()
                currentUnit = currentUnit.append(it)
            }
        }
        if (currentTime.isNotEmpty() && currentUnit.isNotEmpty()) {
            finalTimeMillis += currentTime.toString().toLong() * (UNIT_MILLIS[currentUnit.toString()] ?: 0)
        }

        return finalTimeMillis
    }
}