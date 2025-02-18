package com.library.link_attribution.model.extension.file

//import com.app.config.error.file.FileError
//import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

import android.util.Log
import com.library.link_attribution.logger.DebugLogger

import java.io.*

fun File.toByteArray(): ByteArray? {
    if (exists()) {
        val size = length().toInt()
        val bytes = ByteArray(size)
        try {
            val buf = BufferedInputStream(FileInputStream(this))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bytes
    } else {
        return null
    }
}

fun File.copy(src: File?) {
    FileInputStream(src).use { inputStream ->
        FileOutputStream(this).use { out ->
            // Transfer bytes from in to out
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
        }
    }
}

fun File.logFile() {
    if (this.isDirectory) {
        DebugLogger.d("FILE_LOG", "logFile: dir = ${this.path}, size = ${this.listFiles()?.size}")
        this.listFiles()?.let {
            for (i in it.indices) {
                it[i].logFile()
            }
        }
    } else {
        DebugLogger.d("FILE_LOG", "logFile: file = ${this.path}")
    }
}


fun File?.deleteDir(): Boolean {
    if (this == null) return false

    return if (isDirectory) {
        list()?.forEach { fileName ->
            val file = File(this, fileName)
            val success = file.deleteDir()
            if (success) {
                Log.d("FileExtension", "deleteDir: deleted=>${file.path}")
            }
            if (!success) {
                return false
            }
        }
        delete()
    } else if (isFile) {
        delete()
    } else {
        false
    }
}

//fun File.writeDataOverride(source: ResponseBody?) {
//    try {
//        if (source == null) throw FileError.SaveDataError()
//        val inputStream = source.byteStream()
//        val outputStream = FileOutputStream(this)
//        val buffer = ByteArray(1024)
//        var length: Int
//        while (inputStream.read(buffer).also { length = it } > 0) {
//            outputStream.write(buffer, 0, length)
//        }
//        inputStream.close()
//        outputStream.close()
//    } catch (ex: Throwable) {
//        throw ex
//    }
//}