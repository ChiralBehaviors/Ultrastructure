/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE;

import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

/**
 * @author hhildebrand
 *
 */
public class UuidGenerator {
    public static UUID fromBase64(String id) {
        return toUUID(base64ToByteArray(id));
    }

    public static String nextId() {
        return toBase64(GENERATOR.generate());
    }

    public static String toBase64(UUID id) {
        return byteArrayToBase64(asByteArray(id));
    }

    private static byte[] asByteArray(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return buffer;
    }

    private static byte[] base64ToByteArray(String s) {
        int numFullGroups = 5;
        byte[] result = new byte[16];

        // Translate all full groups from base64 to byte array elements
        int inCursor = 0, outCursor = 0;
        for (int i = 0; i < numFullGroups; i++) {
            int ch0 = base64toInt(s.charAt(inCursor++));
            int ch1 = base64toInt(s.charAt(inCursor++));
            int ch2 = base64toInt(s.charAt(inCursor++));
            int ch3 = base64toInt(s.charAt(inCursor++));
            result[outCursor++] = (byte) (ch0 << 2 | ch1 >> 4);
            result[outCursor++] = (byte) (ch1 << 4 | ch2 >> 2);
            result[outCursor++] = (byte) (ch2 << 6 | ch3);
        }
        int ch0 = base64toInt(s.charAt(inCursor++));
        int ch1 = base64toInt(s.charAt(inCursor++));
        result[outCursor++] = (byte) (ch0 << 2 | ch1 >> 4);
        return result;
    }

    private static int base64toInt(char c) {
        int result = base64ToInt[c];
        if (result < 0) {
            throw new IllegalArgumentException("Illegal character " + c);
        }
        return result;
    }

    private static String byteArrayToBase64(byte[] a) {
        StringBuffer result = new StringBuffer(20);
        int inCursor = 0;
        for (int i = 0; i < 5; i++) {
            int byte0 = a[inCursor++] & 0xff;
            int byte1 = a[inCursor++] & 0xff;
            int byte2 = a[inCursor++] & 0xff;
            result.append(intToBase64[byte0 >> 2]);
            result.append(intToBase64[byte0 << 4 & 0x3f | byte1 >> 4]);
            result.append(intToBase64[byte1 << 2 & 0x3f | byte2 >> 6]);
            result.append(intToBase64[byte2 & 0x3f]);
        }

        int byte0 = a[inCursor++] & 0xff;
        result.append(intToBase64[byte0 >> 2]);
        result.append(intToBase64[byte0 << 4 & 0x3f]);
        return result.toString();
    }

    private static UUID toUUID(byte[] byteArray) {
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = msb << 8 | byteArray[i] & 0xff;
        }
        for (int i = 8; i < 16; i++) {
            lsb = lsb << 8 | byteArray[i] & 0xff;
        }
        UUID result = new UUID(msb, lsb);
        return result;
    }

    private static final byte           base64ToInt[] = { -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59,
            60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1,
            -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
            38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };

    private static final NoArgGenerator GENERATOR     = Generators.timeBasedGenerator();

    private static final char           intToBase64[] = { 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'   };
}
