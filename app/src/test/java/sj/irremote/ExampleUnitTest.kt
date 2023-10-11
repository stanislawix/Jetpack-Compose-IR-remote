package sj.irremote

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
        assertEquals(4, 2 + 2)
    }

    @Test
    fun compareBitReverseFunctions() {
        val hex: Int = 0x46
        assertEquals(reverseBits(hex), reverseAndConvertToDecimal(hex))
    }

    fun reverseBits(hex: Int): Int {
        var reversed = 0
        for (i in 0..7) {
            reversed = reversed shl 1
            reversed = reversed or (hex shr i and 1)
        }
        return reversed
    }

    fun reverseAndConvertToDecimal(n: Int): Int {
        var reversed = 0
        var temp = n

        for (i in 0 until 8) {
            reversed = reversed shl 1 // Shift the result left by one bit
            reversed = reversed or (temp and 1) // Add the least significant bit of temp to reversed
            temp = temp shr 1 // Shift temp to the right by one bit
        }

        return reversed
    }


}