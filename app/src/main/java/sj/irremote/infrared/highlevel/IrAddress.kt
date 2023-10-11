package sj.irremote.infrared.highlevel

import sj.irremote.utils.reverseBits

/**
 * This class is made to contain the IR device address targeted by IR messages.
 *
 * @param hexAddress The IR device address, preferably in hexadecimal format. If not known, some devices just use address = 0.
 * @param reverseAddressBits If true, the given address will be bit-reversed - look up https://oeis.org/A160638.
 */
class IrAddress(private val hexAddress: Int, private val reverseAddressBits: Boolean = true) {
    private val address: Int = if (reverseAddressBits) {
        hexAddress.reverseBits()
    } else {
        hexAddress
    }

    fun toInt() = address

}