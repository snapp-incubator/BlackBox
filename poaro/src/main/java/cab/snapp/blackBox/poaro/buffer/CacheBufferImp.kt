package cab.snapp.blackBox.poaro.buffer

import cab.snapp.blackBox.poaro.Log
import cab.snapp.blackBox.poaro.LogLevel
import java.text.SimpleDateFormat

abstract class CacheBufferImp(
    private val timeFormatter: SimpleDateFormat
) : CacheBuffer {

    val list: MutableList<Log> = mutableListOf()
    val cachedLogs: MutableList<Log> = mutableListOf()
    var firstLoggerCachedTime = 0L
    private var cacheFileName = "bufferCache.log"


    companion object {
        private const val BUFFER_CACHE_STATIC_PART = "_cache"

        const val LOGGER_FILE_STATIC_NAME = "logger"
        const val LOGGER_FILE_SUFFIX = ".log"

        private const val DEBUG = "Debug"
        private const val WARNING = "Warning"
        private const val FORCE = "Force"
        private const val ERROR = "Error"
        private const val USER_INTERACTION = "UserInteraction"
    }

    override suspend fun init(streamName: String) {
        cacheFileName = "${streamName.lowercase()}$BUFFER_CACHE_STATIC_PART$LOGGER_FILE_SUFFIX"
        val bufferCacheData = fileManager.readFile(cacheFileName)
        if (bufferCacheData.isNotEmpty()) {
            val logStrings = bufferCacheData.split(";")
            logStrings.forEachIndexed { index, logString ->
                if (logString.isEmpty()) {
                    return@forEachIndexed
                }
                val log = logString.split(",")
                if (log.size < 4) {
                    return@forEachIndexed
                }
                val date = log[0]
                val level = log[1].trim()
                val label = log[2].trim()
                val message = log[3].trim()

                val detectedLevel = when (level) {
                    DEBUG -> LogLevel.Debug
                    ERROR -> LogLevel.Error
                    WARNING -> LogLevel.Warning
                    USER_INTERACTION -> LogLevel.UserInteraction
                    FORCE -> LogLevel.Force
                    else -> LogLevel.Debug
                }

                val dateLong = timeFormatter.parse(date)
                dateLong?.let { parsedDate ->
                    if (index == 0) {
                        firstLoggerCachedTime = parsedDate.time
                    }
                    val detectedLog = Log(parsedDate.time, detectedLevel, label, message)
                    cachedLogs.add(detectedLog)
                }
            }
            flushCachedLogs()
            clearFile()
        }
    }

    override suspend fun addLog(log: Log) {
        writeLog(log)
        list.add(log)
    }

    override fun clear() {
        list.clear()
        clearFile()
    }

    override fun getSize(): Int {
        return list.size
    }

    private suspend fun writeLog(log: Log) {
        fileManager.writeLogs(
            fileName = cacheFileName,
            logs = listOf(log)
        )
    }

    private fun clearFile() {
        fileManager.clearFile(cacheFileName)
    }

    override fun getaBufferList(): List<Log> {
        return list.toList()
    }
}