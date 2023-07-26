package cab.snapp.blackBox.poaro.flushers

import cab.snapp.blackBox.poaro.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NoLimitFlusher : Flusher {

    private val signal = MutableSharedFlow<List<Log>>()

    override suspend fun onNewLog(log: Log) {
        signal.emit(listOf(log))
    }

    override fun flushSignal(): Flow<List<Log>> {
        return signal.asSharedFlow()
    }
}