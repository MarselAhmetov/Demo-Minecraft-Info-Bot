package ru.demo_bot_minecraft.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BinaryUtils {
    public static int getIntData(DataInputStream dis) {
        try {
            int intData = 0;
            int width = 0;
            while (true) {
                int varInt = dis.readByte();
                intData |= (varInt & 0x7F) << width++ * 7;
                if (width > 5) {
                    throw new IllegalArgumentException();
                }
                if ((varInt & 0x80) != 128) {
                    break;
                }
            }
            return intData;
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe.getMessage());
        }
    }

    public static void sendIntData(DataOutputStream dos, int intData) {
        try {
            while (true) {
                if ((intData & 0xFFFFFF80) == 0) {
                    dos.writeByte(intData);
                    break;
                }
                dos.writeByte(intData & 0x7F | 0x80);
                intData >>>= 7;
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe.getMessage());
        }
    }
}
