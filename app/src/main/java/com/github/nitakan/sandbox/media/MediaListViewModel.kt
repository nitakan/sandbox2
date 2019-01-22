package com.github.nitakan.sandbox.media

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import com.github.nitakan.sandbox.model.MediaStorageUseCase

class MediaListViewModel : ViewModel() {
    private val directories = MutableLiveData<List<ContentDirectory>>()
    private val shownFiles = MutableLiveData<List<MediaContent>>()

    private val useCase = MediaStorageUseCase()
    private val disposable = CompositeDisposable()

    private val defaultItems = listOf(
        ContentDirectory.All,
        ContentDirectory.Pictures,
        ContentDirectory.Videos
    )

    fun requestDir(activity: Activity): LiveData<List<ContentDirectory>> {

        directories.value = defaultItems

        useCase.requestMediaDirectory(activity)
            .subscribeOn(Schedulers.computation())
            .subscribe({
                val list = it.map {item ->
                    ContentDirectory.ContentDirectoryImpl(item.file)
                }

                directories.postValue(defaultItems + list)

            }, {

            }).addTo(disposable)
        return directories
    }

    fun requestFileInDir(activity: Activity, directory: ContentDirectory): LiveData<List<MediaContent>> {

        when (directory) {
            is ContentDirectory.All -> this.useCase.requestAllMediaFile(activity)
            is ContentDirectory.Pictures -> this.useCase.requestAllPictures(activity)
            is ContentDirectory.Videos -> this.useCase.requestAllVideos(activity)
            is ContentDirectory.ContentDirectoryImpl -> {
                this.useCase.requestMediaFileInDirectory(activity, MediaStorageUseCase.MediaDirectory(directory.file))
            }
        }.subscribe({
            shownFiles.postValue(it.map{item ->  MediaContent(MediaContent.Type.Photo, item.file, false) })
        },{

        }).addTo(disposable)

        return shownFiles
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}