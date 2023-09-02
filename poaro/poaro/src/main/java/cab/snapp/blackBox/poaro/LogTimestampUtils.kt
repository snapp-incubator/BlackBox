package cab.snapp.blackBox.poaro

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LogTimestampUtils {

    private val timestampFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

    fun getCalender(): Calendar {
        return Calendar.getInstance()
    }

    fun parseTimestamp(timestampString: String): Long {
        val date: Date = timestampFormat.parse(timestampString)
        return date.time
    }
}
