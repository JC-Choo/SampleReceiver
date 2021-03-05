package kr.co.receiver

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val test00 = "100"
        val test01 = "1,000"
        val test02 = "100,000"
        val test03 = "1.000"

        println("test00 = ${test00.replace()}")
        println("test01 = ${test01.replace()}")
        println("test02 = ${test02.replace()}")
        println("test03 = ${test03.replace()}")
    }

    fun String.replace() = this.replace(",", "")
}