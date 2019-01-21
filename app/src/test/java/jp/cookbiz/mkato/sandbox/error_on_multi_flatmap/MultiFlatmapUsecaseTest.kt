package jp.cookbiz.mkato.sandbox.rx.error_on_multi_flatmap

import org.junit.Test

class MultiFlatmapUsecaseTest {

    private fun request(errorOnFirst: Boolean, errorOnSecond: Boolean, errorOnThird: Boolean) =
        MultiFlatmapUsecase().let { usecase ->
            usecase.requestFirst(errorOnFirst) // Login -> GetRegistryKey -> RegisterKey
                .doOnError {System.out.println("1 doOnError $it")}
                .doOnNext {System.out.println("1 doOnNext $it")}
                .retry(2)
                .flatMap { first -> usecase.requestSecond(first.toData(), errorOnSecond).retry(2) }
                .doOnError {System.out.println("2 doOnError $it")}
                .doOnNext {System.out.println("2 doOnNext $it")}
                .flatMap { second -> usecase.requestThird(second.toData(), errorOnThird).retry(2) }
                .doOnError {System.out.println("2 doOnError $it")}
                .doOnNext {System.out.println("2 doOnNext $it")}
                .map { it.toData().text }
                .singleOrError()
        }

    @Test
    fun noError() {
        this.request(false, false, false)
            .test()
            .assertValue("First,Second,Third")
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun onFirstError() {
        this.request(false, true, true)
            .test()
            .assertError {
                when (it) {
                    is MultiFlatmapUsecase.MultiMapError.FirstError -> false
                    is MultiFlatmapUsecase.MultiMapError.SecondError -> true
                    is MultiFlatmapUsecase.MultiMapError.ThirdError -> false
                    else -> false
                }
            }
            .errors().forEach {
                System.out.println(it.toString())
            }

    }


}





