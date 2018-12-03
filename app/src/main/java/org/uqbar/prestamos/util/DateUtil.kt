package org.uqbar.prestamosapp.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by fernando on 10/27/16.
 */

object DateUtil {

    var FORMATO = "dd/MM/yyyy"

    fun asString(aDate: Date): String {
        return SimpleDateFormat(FORMATO).format(aDate)
    }

    fun asDate(aString: String): Date? {
        try {
            return SimpleDateFormat(FORMATO).parse(aString)
        } catch (e: ParseException) {
            return null
        }

    }

}