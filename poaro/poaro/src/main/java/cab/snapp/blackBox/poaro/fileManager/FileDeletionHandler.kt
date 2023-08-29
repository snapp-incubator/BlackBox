package cab.snapp.blackBox.poaro.fileManager

import java.io.File

interface FileDeletionHandler {
    suspend fun start(directory : File)
    fun stop()
}