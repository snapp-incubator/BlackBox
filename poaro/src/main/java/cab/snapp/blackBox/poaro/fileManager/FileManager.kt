package cab.snapp.blackBox.poaro.fileManager

import cab.snapp.blackBox.poaro.Log

interface FileManager {
    suspend fun writeLogs(fileName: String, logs: List<Log>)
    fun clearFile(fileName: String)
    suspend fun readFile(fileName: String): String
    suspend fun init()
}