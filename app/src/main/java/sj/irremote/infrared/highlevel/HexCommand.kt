package sj.irremote.infrared.highlevel

/**
 * This interface is made to be implemented by all (preferably grouped by controlled devices) IR command enum classes.
 */
interface HexCommand {
    fun toInt(): Int
    fun getEnumName(): String
}
