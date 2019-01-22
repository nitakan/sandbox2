package com.github.nitakan.sandbox.rx.error_on_multi_flatmap

import io.reactivex.Observable


class MultiFlatmapUsecase {

    fun requestFirst(error: Boolean): Observable<ResponseFirst> = Observable.just(error)
        .map {
            if (error) {
                throw MultiMapError.FirstError("Error on First")
            }
            ResponseFirst("First")
        }

    fun requestSecond(first: DataFirst, error: Boolean): Observable<ResponseSecond> = Observable.just(first)
        .map {
            if (error) {
                throw MultiMapError.SecondError("Error on Second")
            }
            ResponseSecond(it.text + ",Second")
        }

    fun requestThird(second: DataSecond, error: Boolean): Observable<ResponseThird> = Observable.just(second)
        .map {
            if (error) {
                throw MultiMapError.ThirdError("Error on Third")
            }
            ResponseThird(it.text + ",Third")
        }

    data class ResponseFirst(val result: String) {
        fun toData() = DataFirst(result)
    }
    data class DataFirst(val text: String)

    data class ResponseSecond(val result: String) {
        fun toData() = DataSecond(result)
    }
    data class DataSecond(val text: String)

    data class ResponseThird(val result: String) {
        fun toData() = DataThird(result)
    }
    data class DataThird(val text: String)

    sealed class MultiMapError(message: String): Throwable(message) {
        class FirstError(message: String): MultiMapError(message)
        class SecondError(message: String): MultiMapError(message)
        class ThirdError(message: String): MultiMapError(message)
    }


}