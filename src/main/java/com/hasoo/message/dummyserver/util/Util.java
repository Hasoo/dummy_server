package com.hasoo.message.dummyserver.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  public static Path getFilePath(String path, String filename) {
    makePath(path);
    return Paths.get(path + File.separatorChar + filename);
  }

  public static Path makePath(String path) {
    File dir = new File(path);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return Paths.get(path);
  }

  public static String convert(ByteBuffer buf) {
    return new String(buf.array(), StandardCharsets.UTF_8).trim();
  }
}
