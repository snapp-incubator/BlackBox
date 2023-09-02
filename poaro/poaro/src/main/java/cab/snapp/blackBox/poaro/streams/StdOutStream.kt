package cab.snapp.blackBox.poaro.streams

import cab.snapp.blackBox.poaro.flushers.Flusher
import cab.snapp.blackBox.poaro.Log
import cab.snapp.blackBox.poaro.LogStream
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectIndexed

class StdOutStream(
    override val flusher: Flusher
) : LogStream {

    companion object {
        const val STREAM_NAME = "STDOUT_STREAM"
    }

    override suspend fun init() {
        flusher.flushSignal().buffer()
            .collectIndexed { _, logs ->
                logs.forEach { log ->
                    println("${log.level.name}::${log.message}")
                }
            }
    }

    override suspend fun send(buffer: List<Log>) {
        buffer.forEach { log ->
            flusher.onNewLog(log)
        }
    }

    override fun getStreamName(): String {
        return STREAM_NAME
    }
}