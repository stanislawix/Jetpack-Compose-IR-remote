package sj.irremote.infrared.lowlevel;

import java.util.ArrayList;
import java.util.List;

public class IRJvcFactory {

    // JVC protocol:
    // LSB first, 1 start bit + 8 bit address + 8 bit command + 1 stop bit

    // NEC protocol:
    // LSB first, 1 start bit + 16 bit address (or 8 bit address and 8 bit inverted address) + 8 bit command + 8 bit inverted command + 1 stop bit

    private static final int ADDRESS_BITS = 8;
    private static final int COMMAND_BITS = 8;

    public static final int JVC_SPACE = 526; // also called UNIT
    public static final int ONE_MARK = 3 * JVC_SPACE;
    public static final int ZERO_MARK = JVC_SPACE;

    public static final int HEADER_MARK = 16 * JVC_SPACE;
    public static final int HEADER_SPACE = 8 * JVC_SPACE; // also called REPEAT_SPACE

    // JVC protocol repeats by skipping the header mark and space
    public static final int REPEAT_TIME = 65000; // also called REPEAT_PERIOD
    public static final int REPEAT_SPACE = 45 * JVC_SPACE; // also called REPEAT_DISTANCE

    public static IRMessage create(int command, int addr, int repeats) {
        List<Integer> message = new ArrayList<>();

        message.add(HEADER_MARK);
        message.add(HEADER_SPACE);

        List<Integer> header1 = decodeInt(addr, ADDRESS_BITS);
//        List<Integer> header2 = decodeInt(~addr, 8);

        List<Integer> data1 = decodeInt(command, COMMAND_BITS);
//        List<Integer> data2 = decodeInt(~command, 8);

        message.addAll(header1);
//        message.addAll(header2);
        message.addAll(data1);
//        message.addAll(data2);
        message.add(JVC_SPACE);

        int messageTime = 0;
        for (int a = 0; a < message.size(); ++a) {
            messageTime += message.get(a).intValue();
        }

        message.add(REPEAT_TIME - messageTime);

        for (int a = 0; a < repeats; a++) {
//            message.add(HDR_MARK);
//            message.add(REPEAT_SPACE);
            message.add(JVC_SPACE);
            message.add(REPEAT_TIME - (HEADER_MARK + REPEAT_SPACE + JVC_SPACE));
        }


        int[] finalCode = new int[message.size()];

        for (int a = 0; a < message.size(); ++a) {
            finalCode[a] = message.get(a).intValue();
        }

        return new IRMessage(IRMessage.FREQ_38_KHZ, finalCode);
    }

    public static IRMessage createRepeat() {
        List<Integer> message = new ArrayList<>();

        message.add(HEADER_MARK);
        message.add(REPEAT_SPACE);
        message.add(JVC_SPACE);
        message.add(REPEAT_TIME - (HEADER_MARK + REPEAT_SPACE + JVC_SPACE));

        int[] finalCode = new int[message.size()];

        for (int a = 0; a < message.size(); ++a) {
            finalCode[a] = message.get(a).intValue();
        }

        return new IRMessage(IRMessage.FREQ_38_KHZ, finalCode);
    }

    private static List<Integer> decodeInt(int num, int bits) {
        List<Integer> values = new ArrayList<>();
        for (int i = bits - 1; i >= 0; i--) {
            values.add(JVC_SPACE);
            values.add(((num & (1 << i)) == 0) ? ZERO_MARK : ONE_MARK);
        }
        return values;
    }
}