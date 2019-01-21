package jp.cookbiz.mkato.sandbox.media

import java.io.File


data class MediaContent(val type: Type, val file: File, var isSelect: Boolean = false) {
    enum class Type {
        Photo,
        Video
    }

    fun getText(): String = file.name
}