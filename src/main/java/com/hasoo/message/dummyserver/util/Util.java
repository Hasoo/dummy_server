package com.hasoo.message.dummyserver.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.io.HexDump;

public class Util {
  public static String dump(String buf)
      throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IOException {
    if (0 == buf.length()) {
      return "";
    }
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      HexDump.dump(buf.getBytes(), 0, output, 0);
      return System.lineSeparator() + output.toString();
    }
  }
}
