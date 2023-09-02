package cab.snapp.blackBox.poaro.buffer

import cab.snapp.blackBox.poaro.fileManager.FileManager
import java.text.SimpleDateFormat

class NetworkCacheBuffer(
    override val fileManager: FileManager,
    timeFormatter: SimpleDateFormat
) : CacheBufferImp(timeFormatter) {

    override suspend fun flushCachedLogs() {
        //todo specific to stream
    }
}