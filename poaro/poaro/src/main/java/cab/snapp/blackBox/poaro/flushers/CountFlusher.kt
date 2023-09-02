package cab.snapp.blackBox.poaro.flushers

import cab.snapp.blackBox.poaro.Log
import cab.snapp.blackBox.poaro.buffer.Buffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class CountFlusher(
    private val countLimit: Int = 10,
    override val buffer: Buffer
) : BufferedFlusher {

    private val signal = MutableSharedFlow<List<Log>>()

    override suspend fun onNewLog(log: Log) {
        buffer.addLog(log)
        if (isCountLimitPassed()) {
            signal.emit(buffer.getaBufferList())
            buffer.clear()
        }
    }

    private fun isCountLimitPassed(): Boolean {
        return buffer.getSize() >= countLimit
    }

    override fun flushSignal(): Flow<List<Log>> {
        return signal.asSharedFlow()
    }
}