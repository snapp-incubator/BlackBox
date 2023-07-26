package cab.snapp.blackBox.poaro


import cab.snapp.blackBox.poaro.flushers.BufferedFlusher
import cab.snapp.blackBox.poaro.buffer.CacheBuffer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.Closeable
import java.util.concurrent.Executors

class SnappLogger(
    private val logsStreams: List<LogStream>,
    private val mode: LogLevel = LogLevel.Debug,
    private val coroutineScope: CoroutineScope = CoroutineScope(
        Executors.newSingleThreadExecutor().asCoroutineDispatcher() +
                CoroutineExceptionHandler { _, exception ->
                    println("logger_exception:  ${exception.printStackTrace()}")
                }
    )
) : Logger, Closeable {

    init {
        coroutineScope.launch {
            logsStreams.forEach {
                (it.flusher as? BufferedFlusher)?.buffer?.let { buffer ->
                    (buffer as? CacheBuffer)?.fileManager?.init()
                    buffer.init(it.getStreamName())
                }
                it.flusher.init(coroutineScope)
                (it as? FileStream)?.fileManager?.init()
                it.init()
            }
        }
    }

    override fun log(
        level: LogLevel,
        label: String,
        message: String,
        selectedStreams: List<String>
    ) {
        if (mode.priority > level.priority) {
            return
        }
        coroutineScope.launch {
            val time = System.currentTimeMillis()
            logsStreams.forEach { logStream ->
                if (selectedStreams.isNotEmpty()) {
                    if (selectedStreams.contains(logStream.getStreamName())) {
                        sendLog(logStream, level, label, message, time)
                    }
                } else {
                    sendLog(logStream, level, label, message, time)
                }
            }
        }
    }

    private suspend fun sendLog(
        logStream: LogStream,
        level: LogLevel,
        label: String,
        message: String,
        time: Long
    ) {
        logStream.send(
            listOf(
                Log(
                    time,
                    level,
                    label,
                    message
                )
            )
        )
    }

    override fun close() {
        coroutineScope.cancel()
    }
}