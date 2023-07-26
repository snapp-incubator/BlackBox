package cab.snapp.blackBox.filestream

import cab.snapp.blackBox.poaro.FileStream
import cab.snapp.blackBox.poaro.flushers.Flusher
import cab.snapp.blackBox.poaro.Log
import cab.snapp.blackBox.poaro.fileManager.FileManager
import kotlinx.coroutines.flow.collectIndexed
import java.util.concurrent.TimeUnit


class FileStreamImpl(
    override val flusher: Flusher,
    override val fileManager: FileManager
) : FileStream {

    companion object {
        private const val LOGGER_FILE_STATIC_NAME = "logger"
        private const val LOGGER_FILE_SUFFIX = ".log"

        const val STREAM_NAME = "FILE_STREAM"
    }

    override suspend fun init() {
        flusher.flushSignal().collectIndexed { _, logs ->
            val loggerFileName =
                "$LOGGER_FILE_STATIC_NAME${getCurrentHalfHour()}$LOGGER_FILE_SUFFIX"
            fileManager.writeLogs(loggerFileName, logs)
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


    private fun getCurrentHalfHour(): Int {
        val hour = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())
        return (hour / 2).toInt()
    }
}