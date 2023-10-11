package sj.irremote.utils

/**
 * Extension function to bit-reverse an 8-bit integer value.
 * @see <a href="https://oeis.org/A160638">bit-reversed 8-bit binary numbers</a> for more information
 */
fun Int.reverseBits(): Int {
    var reversed = 0
    for (i in 0..7) {
        reversed = reversed shl 1
        reversed = reversed or (this shr i and 1)
    }
    return reversed
}