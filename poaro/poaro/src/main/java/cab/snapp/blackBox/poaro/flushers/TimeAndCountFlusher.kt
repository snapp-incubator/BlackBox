package cab.snapp.blackBox.poaro.flushers

import cab.snapp.blackBox.poaro.Log
import cab.snapp.blackBox.poaro.LogLevel
import cab.snapp.blackBox.poaro.buffer.Buffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

class TimeAndCountFlusher(
    private val countLimit: Int = 10,
    private val timeLimit: Int = 10,
    override val buffer: Buffer,
) : BufferedFlusher {

    private val signal = MutableSharedFlow<List<Log>>()
    private lateinit var timeLimitJob: Job
    private val mutex = Mutex()

    override suspend fun init(coroutineScope: CoroutineScope) {
        timeLimitJob = startTimeLimitJob(coroutineScope)
    }

    private fun startTimeLimitJob(coroutineScope: CoroutineScope): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(TimeUnit.SECONDS.toMillis(timeLimit.toLong()))
                coroutineScope.launch {
                    emitAndClearLogs()
                }
            }
        }
    }

    private suspend fun emitAndClearLogs() {
        mutex.withLock {
            val list = buffer.getaBufferList()
            if (list.isNotEmpty()) {
                buffer.clear()
                signal.emit(list)
            }
        }
    }

    override suspend fun onNewLog(log: Log) {
        if (log.level == LogLevel.Force) {
            signal.emit(listOf(log))
        } else {
            buffer.addLog(log)
            if (isCountLimitPassed()) {
                emitAndClearLogs()
            }
        }
    }

    override fun flushSignal(): Flow<List<Log>> {
        return signal.asSharedFlow()
    }

    private fun isCountLimitPassed(): Boolean {
        return buffer.getSize() >= countLimit
    }

    override fun close() {
        timeLimitJob.cancel()
    }
}