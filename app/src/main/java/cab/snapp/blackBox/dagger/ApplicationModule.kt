package cab.snapp.blackBox.dagger

import cab.snapp.blackBox.crashHandler.CrashHandler
import cab.snapp.blackBox.filestream.FileStreamImpl
import cab.snapp.blackBox.loki.LokiStream
import cab.snapp.blackBox.poaro.FileStream
import cab.snapp.blackBox.poaro.LogLevel
import cab.snapp.blackBox.poaro.LogStream
import cab.snapp.blackBox.poaro.SnappLogger
import cab.snapp.blackBox.poaro.buffer.Buffer
import cab.snapp.blackBox.poaro.buffer.FileCacheBuffer
import cab.snapp.blackBox.poaro.buffer.NetworkCacheBuffer
import cab.snapp.blackBox.poaro.fileManager.FileManager
import cab.snapp.blackBox.poaro.fileManager.FileManagerImpl
import cab.snapp.blackBox.poaro.flushers.CountFlusher
import cab.snapp.blackBox.poaro.flushers.Flusher
import cab.snapp.blackBox.poaro.flushers.NoLimitFlusher
import cab.snapp.blackBox.poaro.flushers.TimeAndCountFlusher
import cab.snapp.blackBox.poaro.flushers.TimeFlusher
import cab.snapp.blackBox.poaro.streams.StdOutStream
import dagger.Module
import dagger.Provides
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Singleton


@Module
class ApplicationModule {

    @TimeAndCountLimitFlusher
    @Provides
    @Singleton
    fun provideTimeAndCountFlusher(buffer: Buffer): Flusher {
        return TimeAndCountFlusher(buffer = buffer)
    }

    @TimeLimitFlusher
    @Provides
    @Singleton
    fun provideTimeFlusher(): Flusher {
        return TimeFlusher()
    }

    @LimitLessFlusher
    @Provides
    @Singleton
    fun provideNoLimitFlusher(): Flusher {
        return NoLimitFlusher()
    }

    @Provides
    @Singleton
    fun provideFileStream(
        @TimeAndCountLimitFlusher flusher: Flusher,
        fileManager: FileManager
    ): FileStream {
        return FileStreamImpl(
            flusher = flusher,
            fileManager = fileManager
        )
    }

    @Provides
    @Singleton
    fun provideStdOutStream(@LimitLessFlusher flusher: Flusher): StdOutStream {
        return StdOutStream(
            flusher = flusher
        )
    }

    @Provides
    @Singleton
    fun provideStreams(
        fileStream: FileStream,
        stdOutStream: StdOutStream
    ): Array<LogStream> {
        return arrayOf(
            fileStream,
            stdOutStream
        )
    }

    @Provides
    fun provideLogLevel(): LogLevel {
        return LogLevel.Debug
    }

    @Provides
    @Singleton
    fun provideLogger(streams: Array<LogStream>, logLevel: LogLevel): SnappLogger {
        return SnappLogger(
            streams.toList(),
            logLevel
        )
    }

    @Provides
    @Singleton
    fun provideCrashHandler(logger: SnappLogger): CrashHandler {
        return CrashHandler(logger = logger)
    }


    @Provides
    fun provideFileManager(
        fileDirectory: File,
        timeFormatter: SimpleDateFormat,
    ): FileManager {
        return FileManagerImpl(
            fileDirectory,
            timeFormatter
        )
    }

    @Provides
    @Singleton
    fun provideFileBuffer(
        fileManager: FileManager,
        timeFormatter: SimpleDateFormat
    ): Buffer {
        return FileCacheBuffer(fileManager, timeFormatter)
    }


    @Provides
    fun provideTimeFormatter(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        }
    }


    @Provides
    @Singleton
    fun provideNetworkCacheBuffer(
        fileManager: FileManager,
        timeFormatter: SimpleDateFormat
    ): NetworkCacheBuffer {
        return NetworkCacheBuffer(
            fileManager,
            timeFormatter
        )
    }

    @CountLimitFlusher
    @Provides
    @Singleton
    fun provideNetworkCountFlusher(
        buffer: NetworkCacheBuffer
    ): Flusher {
        return CountFlusher(buffer = buffer)
    }

    @Provides
    @Singleton
    fun provideLokiStream(
        @CountLimitFlusher flusher: Flusher,
    ): LokiStream {
        return LokiStream(
            lokiURL = "",
            appName = "",
            flusher = flusher
        )
    }
}