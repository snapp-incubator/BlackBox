package cab.snapp.blackBox.poaro

import cab.snapp.blackBox.poaro.fileManager.FileManagerImpl
import cab.snapp.blackBox.poaro.fileManager.FileManagerImpl.Companion.LOGGER_FOLDER_NAME
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class LogReportGenerator(private val logFolder: File) : ReportGenerator {

    /**
     * to handle time-related operations, such as retrieving the current timestamp and parsing log timestamps
     */
    private val logTimestampUtils = LogTimestampUtils()

    /**
     * is used to generate the report based on the provided parameters: timeRange and outputFile.
     * timeRange specifies the duration in minutes for which logs should be included in the report
     */
    private fun generateReport(timeRange: Int, outputFile: File) {
        val currentTime = logTimestampUtils.getCalender()
        val startTime = logTimestampUtils.getCalender()
        startTime.add(Calendar.MINUTE, -timeRange)

        val filteredLogs = mutableListOf<String>()

        if (logFolder.isDirectory) {
            val loggerFolder = File(logFolder, LOGGER_FOLDER_NAME)
            val logFiles = loggerFolder.listFiles()
            logFiles?.forEach { logFile ->
                val fileData = logFile.readText()
                val logStrings = fileData.split(";")

                logStrings.forEach { logString ->
                    val log = parseLog(logString)
                    if (log != null && log.timestamp >= startTime.timeInMillis && log.timestamp <= currentTime.timeInMillis) {
                        filteredLogs.add(logString)
                    }
                }
            }
        }

        writeReportToFile(filteredLogs, outputFile)
    }

    override fun createAndGetReport(timeRange: Int, outputFileName: String): File {
        val fileHelper = FileHelper(logFolder)
        val folder = fileHelper.createFolder(FileManagerImpl.REPORT_FOLDER_NAME)
        val file = fileHelper.createFile(folder, outputFileName)
        generateReport(timeRange,file)

        return file
    }


    private fun parseLog(line: String): Log? {
        val parts = line.split(",")

        if (parts.size >= 4) {
            val timestampString = parts[0].trim()
            val levelString = parts[1].trim()
            val label = parts[2].trim()
            val message = parts[3].trim()

            val logLevel = when (levelString) {
                "Error" -> {
                    LogLevel.Error
                }
                "Warning" -> {
                    LogLevel.Warning
                }
                "Debug" -> {
                    LogLevel.Debug
                }
                "UserInteraction" -> {
                    LogLevel.UserInteraction
                }
                else -> {
                    LogLevel.Force
                }
            }

            return Log(logTimestampUtils.parseTimestamp(timestampString), logLevel, label, message)
        }

        return null
    }

    /**
     * uses the FileWriter in a use block to ensure that the file writer is automatically closed after writing the data.
     */
    private fun writeReportToFile(logs: List<String>, outputFile: File) {
        FileOutputStream(outputFile).bufferedWriter().use { writer ->
            logs.forEach { log ->
                writer.write(log)
                writer.flush()
            }
        }
    }
}
