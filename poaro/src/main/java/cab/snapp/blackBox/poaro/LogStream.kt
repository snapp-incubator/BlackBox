package cab.snapp.blackBox.poaro

import cab.snapp.blackBox.poaro.fileManager.FileManager
import cab.snapp.blackBox.poaro.flushers.Flusher


interface LogStream {
    val flusher: Flusher
    suspend fun send(buffer: List<Log>)
    fun getStreamName(): String
    suspend fun init()
}

interface FileStream : LogStream {
    val fileManager: FileManager
}