package com.github.nitakan.sandbox.error_on_sealed_class

import com.github.nitakan.sandbox.sandbox.SampleCaseClassManager
import org.junit.Test

class SealedClassErrorTest {

    @Test
    fun test() {

        val items = SampleCaseClassManager.SampleCaseClass.items

        items.forEach {item ->
            val str = item.get()
            System.out.println(str)
        }

    }


}