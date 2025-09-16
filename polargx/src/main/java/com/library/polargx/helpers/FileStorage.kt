package com.library.polargx.helpers

import android.content.Context
import android.util.Log
import com.library.polargx.Configuration
import java.io.File

data class FileStorage(val file: File) {

    companion object {
        const val TAG = ">>>Polar"

        fun listFiles(directory: FileStorage): List<String> {
            return try {
                directory.file.list()?.toList() ?: emptyList()
            } catch (e: Exception) {
                throw RuntimeException("Error listing files in directory ${directory.file}: ${e.message}")
            }
        }

        fun remove(file: String, directory: FileStorage) {
            val targetFile = directory.file(file)
            if (targetFile.exists()) {
                try {
                    targetFile.delete()
                } catch (e: Exception) {
                    throw RuntimeException("Error deleting file ${targetFile.path}: ${e.message}")
                }
            }
        }

        fun getSDKDirectory(context: Context): FileStorage {
            return getFilesDirDirectory(context).appendingSubDirectory(Configuration.BRAND + "-aRDrdAOPcD")
        }

        private fun getFilesDirDirectory(context: Context): FileStorage {
            val filesDirPath = context.filesDir.absolutePath
            return FileStorage(filesDirPath)
        }
    }

    constructor(filePath: String) : this(File(filePath))

    fun file(name: String): File {
        return File(file, name)
    }

    /**
     * Default is check and create the sub directory, disable it use 'creating = false'
     */
    fun appendingSubDirectory(subDirectory: String, creating: Boolean = true): FileStorage {
        val newFile = File(file, subDirectory)
        if (creating) {
            checkAndCreateDirectories(newFile)
        }
        return FileStorage(newFile)
    }

    private fun checkAndCreateDirectories(file: File) {
        if (!file.exists()) {
            try {
                file.mkdirs()
            } catch (e: Exception) {
                Log.d(TAG, "WARNING: Couldn't create path <${file.path}> with error: ${e.message}")
            }
        }
    }
}
