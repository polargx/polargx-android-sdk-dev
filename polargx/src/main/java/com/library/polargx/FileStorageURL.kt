package com.library.polargx

import java.io.File

data class FileStorageURL(val url: File) {

    constructor(filePath: String) : this(File(filePath))

    fun file(name: String): File {
        return File(url, name)
    }
}