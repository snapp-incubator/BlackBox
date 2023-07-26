package cab.snapp.blackBox.crashHandler

import cab.snapp.blackBox.poaro.SnappLogger
import cab.snapp.blackBox.poaro.f
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CrashHandler @Inject constructor(
    private val logger: SnappLogger
) {
    companion object {
        private const val CRASH_LOG_LABEL = "Crash"
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    fun startWatching() {
        val androidUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread: Thread, throwable: Throwable ->
            val job = scope.launch {
                logNullValue(throwable)
            }
            scope.launch {
                job.join()
                androidUncaughtExceptionHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    private fun logNullValue(t: Throwable) {
        logger.f(CRASH_LOG_LABEL, t.stackTraceToString())
    }
}