package cab.snapp.blackBox.loki

import cab.snapp.blackBox.poaro.flushers.Flusher
import cab.snapp.blackBox.poaro.Log
import cab.snapp.blackBox.poaro.LogStream
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectIndexed
import org.json.JSONException
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.net.ssl.HttpsURLConnection


class LokiStream(
    private val lokiURL: String,
    private val appName: String,
    override val flusher: Flusher
) : LogStream {

    companion object{
         const val STREAM_NAME =  "LOKI_STREAM"
    }
    private val gson = Gson()

    private val timeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun init() {
            flusher.flushSignal().collectIndexed { _, logs ->
                try {
                    val conn = URL("$lokiURL/api/prom/push").openConnection() as HttpsURLConnection
                    conn.connectTimeout = 10000
                    conn.readTimeout = 10000
                    conn.requestMethod = "POST"
                    conn.doInput = true
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json")

                    val requestBodyStream = DataOutputStream(conn.outputStream)

                    val logEntities = mutableListOf<LokiPushRequest.Stream.Entity>()
                    logs.forEach {

                        logEntities.add(
                            LokiPushRequest.Stream.Entity(
                                ts = timeFormatter.format(Date(it.timestamp)),
                                message = it.message
                            )
                        )
                    }
                    val stream = LokiPushRequest.Stream(
                        "{app=\"$appName\"}",
                        logEntities
                    )
                    try {
                        val requestJson = gson.toJson(LokiPushRequest(listOf(stream)))
                        println("RequestBody:$requestJson")
                        requestBodyStream.writeBytes(requestJson)
                        requestBodyStream.flush()
                        requestBodyStream.close()
                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                    conn.connect()

                    val responseCode = conn.responseCode
                    println("ResponseCode:$responseCode")
                    val responseBody = readStream(conn.inputStream)
                    println("ResponseBody: $responseBody")
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
    }
    override suspend fun send(buffer: List<Log>) {
        buffer.forEach { log ->
            flusher.onNewLog(log)
        }
    }

    override fun getStreamName(): String {
        return STREAM_NAME
    }


    private fun readStream(inputStream: InputStream?): String {
        var reader: BufferedReader? = null
        val response = StringBuffer()
        try {
            reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return response.toString()
    }
}