package com.hasoo.dummyserver.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.io.HexDump;

public class HUtil {
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

  public static String getStackTrace(final Throwable e) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw, true);
    e.printStackTrace(pw);
    return sw.getBuffer().toString();
  }

  public static String getCurrentDate12() {
    return new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
  }

  public static byte[] serializeObj(Object obj) throws IOException {
    byte[] serialized = null;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
        // not able to serialize inner class, so Human class is not
        oos.writeObject(obj);
        serialized = baos.toByteArray();
      }
    }
    return serialized;
  }

  public static Object deserializedObj(byte[] b) throws IOException, ClassNotFoundException {
    Object o = null;
    try (ByteArrayInputStream bais = new ByteArrayInputStream(b)) {
      try (ObjectInputStream ois = new ObjectInputStream(bais)) {
        o = ois.readObject();
      }
    }
    return o;
  }

  public static Date getDate14(String yyyymmddhh24miss) {
    final SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
    try {
      return dt.parse(yyyymmddhh24miss);
    } catch (ParseException e) {
    }
    return null;
  }

  public static Properties getProperties(String filename)
      throws FileNotFoundException, IOException {
    final Properties properties = new Properties();
    try (final FileInputStream fis = new FileInputStream(filename)) {
      properties.load(fis);
    }
    return properties;
  }
}
