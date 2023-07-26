package cab.snapp.blackBox

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import cab.snapp.blackBox.poaro.buffer.Buffer
import cab.snapp.blackBox.poaro.buffer.FileCacheBuffer
import cab.snapp.blackBox.poaro.fileManager.FileDeletionHandler
import cab.snapp.blackBox.poaro.fileManager.FileDeletionHandlerImpl
import cab.snapp.blackBox.poaro.fileManager.FileManager
import cab.snapp.blackBox.poaro.fileManager.FileManagerImpl
import cab.snapp.blackBox.filestream.FileStreamImpl
import cab.snapp.blackBox.poaro.SnappLogger
import cab.snapp.blackBox.poaro.Log
import cab.snapp.blackBox.poaro.LogLevel
import cab.snapp.blackBox.poaro.LogStream
import cab.snapp.blackBox.poaro.d
import cab.snapp.blackBox.poaro.flushers.TimeAndCountFlusher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class SnappLoggerTest {

    private lateinit var fileManager: FileManager
    private lateinit var fileDeletionHandler: FileDeletionHandler
    private lateinit var fileDirectory: File

    private lateinit var logger: SnappLogger
    private lateinit var stream: LogStream
    private lateinit var flusher: TimeAndCountFlusher
    private lateinit var buffer: Buffer

    private val loggerFolderTestName = "logger_test"

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val timeFormatter =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        }

    private val loggerFolderTest = File(appContext.filesDir, loggerFolderTestName)


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        fileDirectory = appContext.filesDir
        fileDeletionHandler = FileDeletionHandlerImpl(1)
        fileManager = FileManagerImpl(fileDeletionHandler,fileDirectory, timeFormatter, loggerFolderTestName)
        buffer = FileCacheBuffer(fileManager, timeFormatter)
        flusher = TimeAndCountFlusher(buffer = buffer)
        stream = FileStreamImpl(flusher, fileManager)
        logger = SnappLogger(
            listOf(stream),
            coroutineScope = CoroutineScope(UnconfinedTestDispatcher())
        )
    }

    @After
    fun cleanup() {
        // Clear any created log files
        if (loggerFolderTest.exists()) {
            loggerFolderTest.listFiles()?.forEach { it.delete() }
            loggerFolderTest.delete()
        }
    }

    @Test
    fun testWriteLogs() = runTest{
        val logs = mutableListOf<Log>()

        for (i in 1..1000) {
            val log = Log(System.currentTimeMillis(), LogLevel.Debug, "TAG", "Log message $i")
            logs.add(log)
        }

        val fileName = "test_log.txt"

        fileManager.init()
        fileManager.writeLogs(fileName, logs)
        val logContent: String = fileManager.readFile(fileName)

        if (logContent.isEmpty()){
            assert(false)
        }

        for (i in 1..1000) {
            val logMessage = "Log message $i"
            assert(logContent.contains(logMessage))
        }
    }

    @Test
    fun testHighTrafficLogs() {
        for (i in 1..300) {
            logger.d("label1", "message1")
        }

        for (i in 1..300) {
            logger.d("label2", "message2")
        }

        for (i in 1..300) {
            logger.d("label3", "message3")
        }

        loggerFolderTest.listFiles()!!.let {files ->
            if (files.isNotEmpty()) {
                files.forEach {file ->
                    if (file.name.contains("logger")){
                        val fileReader = FileReader(file)
                        val reader = BufferedReader(fileReader)
                        val fileData = StringBuilder("")
                        var line = reader.readLine()
                        while (line != null) {
                            fileData.append(line)
                            line = reader.readLine()
                        }
                        reader.close()

                        val logs = fileData.toString().split(";").filter { logString ->
                            logString.isNotEmpty()
                        }

                        assert(logs.size == 900)
                    }
                }
            }
        }
    }
}
