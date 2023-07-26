package cab.snapp.blackBox.poaro.fileManager

import cab.snapp.blackBox.poaro.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.Date


class FileManagerImpl(
    private val fileDirectory: File,
    private val timeFormatter: SimpleDateFormat,
    private val loggerFolderName: String = LOGGER_FOLDER_NAME
) : FileManager {

    companion object {
        const val LOGGER_FOLDER_NAME = "logger"
    }

    private lateinit var fileReader: FileReader
    private lateinit var reader: BufferedReader
    private val lastReadFileName = ""

    private lateinit var fileWriter: FileWriter
    private lateinit var writer: BufferedWriter

    private lateinit var cacheFile: File
    private var lastCacheFileName = ""

    private lateinit var loggerFolder: File
    private lateinit var file: File

    private var lastFileName = ""

    override suspend fun init() {
        loggerFolder = File(fileDirectory, loggerFolderName)
        if (!loggerFolder.exists()) {
            loggerFolder.mkdir().also { created ->
                if (!created) {
                    return
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeLogs(fileName: String, logs: List<Log>) {
        handleFileCreation(fileName)
        logs.forEach {
            val time = timeFormatter.format(Date(it.timestamp))
            writer.append("$time, ${it.level}, ${it.label}, ${it.message};\n")
        }
        writer.flush()
    }

    override fun clearFile(fileName: String) {
        if (fileName != lastCacheFileName) {
            cacheFile = File(loggerFolder, fileName)
        }
        val randomAccessFile = RandomAccessFile(cacheFile, "rw")
        randomAccessFile.setLength(0)

    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun readFile(fileName: String): String {
        if (fileName != lastReadFileName) {
            file = File(loggerFolder, fileName)
            if (file.exists().not()) {
                return ""
            }
            fileReader = FileReader(file)
            reader = BufferedReader(fileReader)
        }

        val fileData = StringBuilder("")
        var line = reader.readLine()
        while (line != null) {
            fileData.append(line)
            line = reader.readLine()
        }
        reader.close()
        return fileData.toString()

    }


    private fun handleFileCreation(fileName: String) {
        if (fileName != lastFileName) {
            if (lastFileName != "") {
                writer.close()
            }
            file = File(loggerFolder, fileName)
            fileWriter = FileWriter(file, true)
            writer = BufferedWriter(fileWriter)
            lastFileName = fileName
        }
    }
}