package cab.snapp.blackBox.poaro.flushers

import cab.snapp.blackBox.poaro.Log
import cab.snapp.blackBox.poaro.buffer.Buffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface Flusher {
    suspend fun onNewLog(log: Log)
    fun flushSignal(): Flow<List<Log>>
    suspend fun init(coroutineScope: CoroutineScope) {}
    fun close() {}
}

interface BufferedFlusher : Flusher {
    val buffer: Buffer
}