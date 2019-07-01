package com.github.nitakan.sandbox.sandbox

class SampleCaseClassManager {

    sealed class SampleCaseClass {

        companion object {
            val items: List<SampleCaseClass> = listOf(SampleCaseClass.Hoge, Piyo)
        }

        abstract fun get(): String

        object Hoge : SampleCaseClass() {
            override fun get() = "HOGE"
        }

        object Piyo : SampleCaseClass() {
            override fun get() = "PIYO"
        }

        class Fuga(private val v: String) : SampleCaseClass() {
            override fun get() = v
        }
    }
}

