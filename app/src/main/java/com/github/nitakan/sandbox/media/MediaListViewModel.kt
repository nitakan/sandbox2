package com.github.nitakan.sandbox.media

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.nitakan.sandbox.model.MediaStorageUseCase
import com.github.nitakan.sandbox.model.MediaStoreManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.io.File

class MediaListViewModel : ViewModel() {
    private val directories = MutableLiveData<List<MediaStoreManager.RequestType>>()
    private val shownFiles = MutableLiveData<List<MediaContent>>()

    private val useCase = MediaStorageUseCase()
    private val disposable = CompositeDisposable()

    private val defaultItems = listOf(
        ContentDirectory.All,
        ContentDirectory.Pictures,
        ContentDirectory.Videos
    )


    fun requestFileInDir(activity: Activity, directory: ContentDirectory): LiveData<List<MediaContent>> {

        when (directory) {
            is ContentDirectory.All -> this.useCase.requestAllMediaFile(activity)
            is ContentDirectory.Pictures -> this.useCase.requestAllPictures(activity)
            is ContentDirectory.Videos -> this.useCase.requestAllVideos(activity)
            is ContentDirectory.ContentDirectoryImpl -> {
                this.useCase.requestMediaFileInDirectory(activity, MediaStorageUseCase.MediaDirectory(directory.file))
            }
        }.subscribe({
            shownFiles.postValue(it.map { item -> MediaContent(MediaContent.Type.Photo, item.file, false) })
        }, {
            Toast.makeText(activity, "${it.message}", Toast.LENGTH_LONG).show()
        }).addTo(disposable)

        return shownFiles
    }

    fun getFileList(): LiveData<List<MediaContent>> = this.shownFiles

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    val manager = MediaStoreManager()

    fun requestDir2(activity: FragmentActivity): LiveData<List<MediaStoreManager.RequestType>> {

        manager.requestAllDirectory(activity)
            .subscribeOn(Schedulers.io())
            .map { MediaStoreManager.RequestType.FolderContent(it) }
            .toList()
            .doOnSubscribe { System.out.println("MediaStoreManager:requestDir2:doOnSubscribe: ${System.currentTimeMillis()}") }
            .doFinally { System.out.println("MediaStoreManager:requestDir2:doFinally: ${System.currentTimeMillis()}") }
            .subscribe({
                directories.postValue(MediaStoreManager.RequestType.defaultRequestType + it)
            }, {
                System.err.println(it)
            }).addTo(this.disposable)

        return directories
    }

    fun requestFileInDir2(activity: AppCompatActivity, directory: MediaStoreManager.RequestType) {
        manager.requestMediaFiles(activity, directory)
            .subscribeOn(Schedulers.io())
            .toList()
            .doOnSubscribe { System.out.println("MediaStoreManager:requestFileInDir2:doOnSubscribe: ${System.currentTimeMillis()}") }
            .doFinally { System.out.println("MediaStoreManager:requestFileInDir2:doFinally: ${System.currentTimeMillis()}") }
            .subscribe({
                shownFiles.postValue(it.map { item -> MediaContent(MediaContent.Type.Photo, item, false) })
            }, {
                Toast.makeText(activity, "${it.message}", Toast.LENGTH_LONG).show()
            }).addTo(disposable)
    }
}