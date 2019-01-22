package com.github.nitakan.sandbox.model

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File

class MediaStorageUseCase {

    private fun requestMediaFiles(activity: Activity): Observable<File> =
        Observable.create<String> { emitter ->
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
            )

            val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

            val queryUri = MediaStore.Files.getContentUri("external")

            val cursorLoader = CursorLoader(
                activity,
                queryUri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATA + " DESC"
            )

            val cur = cursorLoader.loadInBackground()

            cur?.let { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val item = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                        emitter.onNext(item)
                    } while (cursor.moveToNext())
                }
            }
            emitter.onComplete()
        }.map { File(it) }

    fun requestAllMediaFile(activity: Activity): Single<List<MediaFile>> =
            this.requestMediaFiles(activity)
                .map { MediaFile(it) }
                .toList()

    fun requestAllPictures(activity: Activity): Single<List<MediaFile>> =
        Observable.create<String> { emitter ->
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
            )

            val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)

            val queryUri = MediaStore.Files.getContentUri("external")

            val cursorLoader = CursorLoader(
                activity,
                queryUri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATA + " DESC"
            )

            val cur = cursorLoader.loadInBackground()

            cur?.let { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val item = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                        emitter.onNext(item)
                    } while (cursor.moveToNext())
                }
            }
            emitter.onComplete()
        }.map { File(it) }
            .map { MediaFile(it) }
            .toList()

    fun requestAllVideos(activity: Activity): Single<List<MediaFile>> =
        Observable.create<String> { emitter ->
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
            )

            val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

            val queryUri = MediaStore.Files.getContentUri("external")

            val cursorLoader = CursorLoader(
                activity,
                queryUri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATA + " DESC"
            )

            val cur = cursorLoader.loadInBackground()

            cur?.let { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val item = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                        emitter.onNext(item)
                    } while (cursor.moveToNext())
                }
            }
            emitter.onComplete()
        }.map { File(it) }
            .map { MediaFile(it) }
            .toList()

    fun requestMediaFileInDirectory(activity: Activity, directory: MediaDirectory): Single<List<MediaFile>> =
        this.requestMediaFiles(activity)
            .map { MediaFile(it) }
            .filter { directory.isChild(it) }
            .toList()

    fun requestMediaDirectory(activity: Activity): Single<List<MediaDirectory>> =
        this.requestMediaFiles(activity)
            .map { item -> item.parentFile }
            .distinctUntilChanged()
            .map { MediaDirectory(it) }
            .toList()


    data class MediaDirectory(val file: File) {
        val name: String
            get() = file.name

        fun isChild(child: MediaFile) = child.file.parentFile == file
    }

    data class MediaFile(val file: File) {
        val uri: Uri
            get() = Uri.fromFile(file)
    }
}