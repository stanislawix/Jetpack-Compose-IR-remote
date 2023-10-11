package sj.irremote.infrared.highlevel

import sj.irremote.utils.reverseBits

/**
 * This enum contains (not all - it's a work in progress thing) IR command codes for the JVC MX-S20 (model CA-S20BK in my case) audio system - with which JVC RM-SES20U infrared remote is meant to be used.
 * The codes were obtained using an Arduino Uno board with an IR receiver.
 *
 * @param hexValue The IR command code, preferably in hexadecimal format.
 * @param reverseCommandBits If true, the given code will be bit-reversed - look up https://oeis.org/A160638.
 */
enum class JvcCommand(private val hexValue: Int, private val reverseCommandBits: Boolean = true):
    HexCommand {
    ON_OFF(0x17),
    VOLUME_UP(0x1E),
    VOLUME_DOWN(0x1F),
    NUMBER_1(0x21),
    NUMBER_2(0x22),
    NUMBER_3(0x23),
    NUMBER_4(0x24),
    NUMBER_5(0x25),
    NUMBER_6(0x26),
    NUMBER_7(0x27),
    NUMBER_8(0x28),
    NUMBER_9(0x29),
    NUMBER_10(0x2A),
    NUMBER_10_PLUS(0x2B),
    Cx2C(0x2C),
    Cx2D(0x2D),
    Cx2E(0x2E),
    Cx2F(0x2F),
    Cx30(0x30),
    Cx31(0x31),
    Cx32(0x32),
    Cx33(0x33),
    Cx34(0x34),
    Cx35(0x35),
    Cx36(0x36),
    Cx37(0x37),
    Cx38(0x38),
    Cx39(0x39),
    Cx3A(0x3A),
    Cx3B(0x3B),
    Cx3C(0x3C),
    Cx3D(0x3D),
    Cx3E(0x3E),
    Cx3F(0x3F),


    AUX_INPUT(0x0),
    ;

    override fun toInt() = if (reverseCommandBits) hexValue.reverseBits() else hexValue
    override fun getEnumName() = name
}