package jp.cookbiz.mkato.sandbox.directories

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.CursorLoader
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import jp.cookbiz.mkato.sandbox.model.MediaStorageUseCase
import java.io.File

class MediaDirectoriesViewModel(val application: Application) : ViewModel() {

    private val useCase = MediaStorageUseCase()

    private val disposables = CompositeDisposable()

    private val directoryList = MutableLiveData<List<MediaStorageUseCase.MediaDirectory>>()

    fun getDirectoryList(): LiveData<List<MediaStorageUseCase.MediaDirectory>> = directoryList

    fun requestDirectoryList(context: Context) {
        Single.create<List<MediaStorageUseCase.MediaDirectory>> { emitter ->

            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)?.listFiles { target ->
                target != null
            }?.filter { file ->
                file.isDirectory && file.listFiles().isNotEmpty()
            }?.map { file ->
                MediaStorageUseCase.MediaDirectory(file)
            }?.also {
                emitter.onSuccess(it)
            } ?: throw NullPointerException("External storage returns a null")

        }.subscribe({ list ->
            directoryList.postValue(list)
        }, {
            System.out.println(it)
        }).dispose()

    }

    fun getMediaStorageDirectoryUri(activity: Activity) {
        useCase.requestMediaDirectory(activity)
            .subscribeOn(Schedulers.io())
            .subscribe({dirs ->
                directoryList.postValue(dirs)
            }, {

            }).addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        this.disposables.clear()
    }

    class MyViewModelProvider(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MediaDirectoriesViewModel(application) as T
        }
    }

}