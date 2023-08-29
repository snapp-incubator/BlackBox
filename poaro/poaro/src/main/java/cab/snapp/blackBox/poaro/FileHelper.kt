package cab.snapp.blackBox.poaro

import java.io.File

class FileHelper(private val file: File) {

    fun createFolder(folderName: String): File {
        val folder = File(file, folderName)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    fun createFile(folder: File, fileName: String): File {
        val file = File(folder, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}
