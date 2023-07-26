package cab.snapp.blackBox.poaro.flushers

import cab.snapp.blackBox.poaro.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TimeFlusher(private val timeLimit: Int = 10) : Flusher {

    private val buffer = mutableListOf<Log>()
    private val signal = MutableSharedFlow<List<Log>>()
    private lateinit var timeLimitJob: Job

    override suspend fun init(coroutineScope: CoroutineScope) {
        timeLimitJob = startTimeLimitJob(coroutineScope)
    }

    private fun startTimeLimitJob(coroutineScope: CoroutineScope): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(TimeUnit.SECONDS.toMillis(timeLimit.toLong()))
                coroutineScope.launch {
                    if (buffer.isNotEmpty()) {
                        signal.emit(buffer.toList())
                        buffer.clear()
                    }
                }
            }
        }
    }

    override suspend fun onNewLog(log: Log) {
        buffer.add(log)
    }

    override fun flushSignal(): Flow<List<Log>> {
        return signal.asSharedFlow()
    }

    override fun close() {
        timeLimitJob.cancel()
    }
}