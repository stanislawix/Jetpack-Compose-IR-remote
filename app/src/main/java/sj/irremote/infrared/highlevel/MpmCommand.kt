package sj.irremote.infrared.highlevel

import sj.irremote.utils.reverseBits

/**
 * This enum contains IR command codes for the MPM MWP-19 fan.
 * The codes were obtained using an Arduino Uno board with an IR receiver, receiving commands from the original IR remote.
 *
 * @param hexValue The IR command code, preferably in hexadecimal format.
 * @param reverseCommandBits If true, the given code will be bit-reversed - look up https://oeis.org/A160638.
 */
enum class MpmCommand(private val hexValue: Int, private val reverseCommandBits: Boolean = true) :
    HexCommand {
    OFF(0x46),
    ON_SPEED(0x44),
    MODE(0x15),
    TIMER(0x16),
    SWING(0x8);

    override fun toInt() = if (reverseCommandBits) hexValue.reverseBits() else hexValue
    override fun getEnumName() = name
}
