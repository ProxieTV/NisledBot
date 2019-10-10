package de.proxietv.nisledbot.utils

import java.util.*
import java.util.regex.Pattern

object StringUtils {

    private val ESCAPE_CHARACTERS: List<Char>
    private val URL_REGEX: String


    init {
        ESCAPE_CHARACTERS = Arrays.asList('*', '_', '`', '~')
        URL_REGEX =
            "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)"
    }

    /**
     * Replace all forbidden characters
     */
    fun replaceCharacter(input: String): String {
        val stringBuilder = StringBuilder(input)
        var i = 0
        while (i < stringBuilder.length) {
            val c = stringBuilder[i]
            if (ESCAPE_CHARACTERS.contains(c)) {
                stringBuilder.replace(i, i + 1, "\\" + c)
                i += 1
            }
            i++
        }
        return stringBuilder.toString()
    }

    /**
     *
     * @param content The message to check
     * @return If the given message contains a link
     */
    fun containtsURL(content: String): Boolean {
        val p = Pattern.compile(URL_REGEX)
        val m = p.matcher(content)
        return m.find()
    }

}
