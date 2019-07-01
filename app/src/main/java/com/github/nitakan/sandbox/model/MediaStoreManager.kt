package com.github.nitakan.sandbox.model

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.database.DataSetObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File

class MediaStoreManager {

    companion object {
        private const val MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE

        private val CONTENT_URI = MediaStore.Files.getContentUri("external")

        private val PROJECTIONS = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE
        )

        private const val SORT_ORDER = "${MediaStore.Files.FileColumns.DATE_ADDED} desc"

        private const val LOADER_ID_ALL_DIRECTORY = 1

        private const val LOADER_ID_MEDIA = 2
    }

    private var findDirectoryStream: Subject<File> = PublishSubject.create()
    private var mediaFilesStream: Subject<File> = PublishSubject.create()

    private lateinit var manager: LoaderManager
    private fun getManager(context: FragmentActivity) =
        if (this::manager.isInitialized) {
            manager
        } else {
            LoaderManager.getInstance(context).also {
                manager = it
            }
        }

    fun requestAllDirectory(context: FragmentActivity): Observable<File> {
        if (!findDirectoryStream.hasComplete()) {
            findDirectoryStream.onComplete()
        }
        findDirectoryStream = PublishSubject.create()

        getManager(context)
            .restartLoader(LOADER_ID_ALL_DIRECTORY, null, getDirectoryLoaderCallback(context))

        return findDirectoryStream
            .sorted()
            .distinctUntilChanged()
    }

    fun requestMediaFiles(activity: FragmentActivity, contentType: RequestType): Observable<File> {

        if (!mediaFilesStream.hasComplete()) {
            mediaFilesStream.onComplete()
        }
        mediaFilesStream = PublishSubject.create()

        getManager(activity)
            .restartLoader(LOADER_ID_MEDIA, null, getMediaFileLoaderCallback(activity, contentType))

        return mediaFilesStream
            .filter { contentType.filter(it) }
    }

    private fun Cursor.getParentDirectory(): File? {
        return getPath()?.let {
            return File(it).parentFile
        }
    }

    private fun Cursor.getPath(): String? {
        return this.getString(this.getColumnIndex((MediaStore.Files.FileColumns.DATA)))
    }

    private fun getDirectoryLoaderCallback(context: Context): LoaderManager.LoaderCallbacks<Cursor> =
        object : LoaderManager.LoaderCallbacks<Cursor> {
            override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(
                context,
                CONTENT_URI,
                PROJECTIONS,
                RequestType.All.getSelection(),
                RequestType.All.getSelectionArgs(),
                SORT_ORDER
            )

            override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                if (loader.isStarted && data != null && !data.isClosed) {
                    data.moveToFirst()
                    while (data.moveToNext()) {
                        data.getParentDirectory()?.also {
                            findDirectoryStream.onNext(it)
                        }
                    }
                }
                findDirectoryStream.onComplete()
            }

            override fun onLoaderReset(loader: Loader<Cursor>) {
                findDirectoryStream.onComplete()
            }
        }



    private fun getMediaFileLoaderCallback(
        context: Context,
        contentType: RequestType
    ): LoaderManager.LoaderCallbacks<Cursor> =
        object : LoaderManager.LoaderCallbacks<Cursor> {
            override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(
                context,
                CONTENT_URI,
                PROJECTIONS,
                contentType.getSelection(),
                contentType.getSelectionArgs(),
                MediaStore.Files.FileColumns.DATE_ADDED + " desc"
            )

            override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                try {
                    if (loader.isStarted && data != null && !data.isClosed) {

                        data.setNotificationUri(context.contentResolver, CONTENT_URI)

                        data.registerContentObserver(object: ContentObserver(Handler()) {
                            override fun onChange(selfChange: Boolean) {
                                super.onChange(selfChange)

                            }
                        })

                        data.moveToFirst()
                        while (data.moveToNext()) {
                            data.getPath()?.also {
                                mediaFilesStream.onNext(File(it))
                            }
                        }
                    }
                    mediaFilesStream.onComplete()
                } catch (e: Exception) {
                    mediaFilesStream.onError(e)
                }
            }

            override fun onLoaderReset(loader: Loader<Cursor>) {
                mediaFilesStream.onComplete()
            }
        }

    enum class MediaContent constructor(internal val type: Int, internal val uri: Uri) {
        UNKNOWN(-1, Uri.EMPTY),
        IMAGE(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ),
        VIDEO(
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        );


        companion object {

            internal fun find(type: Int): MediaContent {
                for (c in MediaContent.values()) {
                    if (c.type == type) {
                        return c
                    }
                }
                return UNKNOWN
            }
        }
    }

    sealed class RequestType {
        companion object {
            private val SELECTION_PHOTO = "$MEDIA_TYPE = ${MediaContent.IMAGE.type}"
            private val SELECTION_VIDEO = "$MEDIA_TYPE = ${MediaContent.VIDEO.type}"
            private val SELECTION_MEDIA = "$SELECTION_PHOTO  OR  $SELECTION_VIDEO"
            val defaultRequestType: List<RequestType>
                get() = listOf(
                    RequestType.All, Pictures, Movies
                )
        }

        abstract fun getSelection(): String
        abstract fun getSelectionArgs(): Array<String>?
        abstract fun filter(file: File?): Boolean

        object All : RequestType() {
            override fun getSelection() = SELECTION_MEDIA
            override fun getSelectionArgs(): Array<String>? = null
            override fun filter(file: File?) = true
        }
        object Movies : RequestType(){
            override fun getSelection() = SELECTION_VIDEO
            override fun getSelectionArgs(): Array<String>? = null
            override fun filter(file: File?) = true
        }
        object Pictures : RequestType(){
            override fun getSelection() = SELECTION_PHOTO
            override fun getSelectionArgs(): Array<String>? = null
            override fun filter(file: File?) = true
        }
        class FolderContent(val folder: File) : RequestType() {
            override fun getSelection() = SELECTION_MEDIA //"($SELECTION_MEDIA) AND _data like ?"
            override fun getSelectionArgs(): Array<String>? {
                return null //arrayOf("'${folder.path}%")
            }
            override fun filter(file: File?) = isParent(file)
            private fun isParent(file: File?): Boolean = file?.parentFile == folder
        }
    }
}