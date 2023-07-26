package cab.snapp.blackBox.poaro.fileManager

import kotlinx.coroutines.*
import java.io.File
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class FileDeletionHandlerImpl(
    private val hours: Int,
    private val fileDeletionCoroutineScope: CoroutineScope = CoroutineScope(
        Executors.newSingleThreadExecutor().asCoroutineDispatcher() +
                CoroutineExceptionHandler { _, exception ->
                    println("logger_exception:  ${exception.printStackTrace()}")
                }
    )
) : FileDeletionHandler {
    /**
     * A mutable list that holds the files to be deleted.
     */
    private val deletionQueue = mutableListOf<File>()

    /**
     * A reference to the cleanup job that runs periodically to delete files.
     */
    private var cleanupJob: Job? = null

    /**
     * Starts the deletion process by launching a cleanup job that runs every 2 hours.
     * It checks the deletion queue periodically and deletes the files in batches.
     */
    private fun startDeletionProcess() {
        if (cleanupJob == null) {
            cleanupJob = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    fileDeletionCoroutineScope.launch {
                        batchCollectAndDeleteFiles()
                    }
                    delay(TimeUnit.HOURS.toMillis(2))
                }
            }
        }
    }

    /**
     * Stops the deletion process by canceling the cleanup job.
     */
    override fun stop() {
        cleanupJob?.cancel()
        cleanupJob = null
    }

    /**
     * Collects the files in the specified directory that were created before the specified duration (in hours).
     * It adds these files to the deletion queue and starts the deletion process if it's not already running.
     */
    override suspend fun start(directory: File) {
        fileDeletionCoroutineScope.launch {
            val currentTime = Date()

            if (directory.exists() && directory.isDirectory) {
                directory.listFiles()?.let { files ->
                    files.forEach { file ->
                        val lastModificationTime = Date(file.lastModified())
                        val hoursDiff = getHoursDifference(lastModificationTime, currentTime)
                        if (hoursDiff >= hours) {
                            deletionQueue.add(file)
                        }
                    }
                }
                startDeletionProcess()
            }
        }
    }

    /**
     * Collects files from the deletion queue in batches and initiates their deletion.
     * It ensures that only one batch is processed at a time using the deletionMutex.
     */
    private fun batchCollectAndDeleteFiles() {
        val filesToDelete = mutableListOf<File>()
        filesToDelete.addAll(deletionQueue)
        deletionQueue.clear()

        if (filesToDelete.isNotEmpty()) {
            deleteFilesInParallel(filesToDelete)
        }
    }

    /**
     * Deletes the files in the specified list in parallel using coroutines.
     * It launches deletion tasks for each file and awaits their completion.
     */
    private fun deleteFilesInParallel(filesToDelete: List<File>) {
        filesToDelete.map { file ->
            file.delete()
            println("Deleted file: $file")
        }
    }

    private fun getHoursDifference(startTime: Date, endTime: Date): Long {
        val diffInMillis = endTime.time - startTime.time
        return TimeUnit.MILLISECONDS.toHours(diffInMillis)
    }
}
