package cab.snapp.blackBox.poaro.buffer


import cab.snapp.blackBox.poaro.fileManager.FileManager
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class FileCacheBuffer(
    override val fileManager: FileManager,
    timeFormatter: SimpleDateFormat
) : CacheBufferImp(timeFormatter) {

    override suspend fun flushCachedLogs() {
        val time = TimeUnit.MILLISECONDS.toHours(firstLoggerCachedTime) / 2
        val loggerFileName = "$LOGGER_FILE_STATIC_NAME$time$LOGGER_FILE_SUFFIX"
        fileManager.writeLogs(
            fileName = loggerFileName,
            logs = cachedLogs
        )
    }
}