package cab.snapp.blackBox.crashHandler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrashHandler(
    private val onCrashCaught: suspend (Throwable) -> Unit
) {

    private val scope = CoroutineScope(Dispatchers.IO)
    fun startWatching() {
        val androidUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread: Thread, throwable: Throwable ->
            val job = scope.launch {
                onCrashCaught(throwable)
            }
            scope.launch {
                job.join()
                androidUncaughtExceptionHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}