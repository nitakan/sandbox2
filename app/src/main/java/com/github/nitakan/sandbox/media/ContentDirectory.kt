package com.github.nitakan.sandbox.media

import android.net.Uri
import java.io.File

sealed class ContentDirectory {
    object All: ContentDirectory()
    object Pictures: ContentDirectory()
    object Videos: ContentDirectory()

    data class ContentDirectoryImpl(val file: File): ContentDirectory() {
        fun getDirectoryName() = if (file.isDirectory) file.name else file.parentFile.name
    }
}
