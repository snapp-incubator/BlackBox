package cab.snapp.blackBox.poaro.buffer

import cab.snapp.blackBox.poaro.Log
import cab.snapp.blackBox.poaro.fileManager.FileManager

interface Buffer {
    suspend fun addLog(log: Log)
    fun clear()
    suspend fun init(streamName: String)
    fun getaBufferList(): List<Log>
    fun getSize(): Int
}

interface CacheBuffer : Buffer {
    val fileManager: FileManager
    suspend fun flushCachedLogs()
}