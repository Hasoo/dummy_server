package com.hasoo.message.dummyserver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import com.squareup.tape.FileObjectQueue;
import com.squareup.tape.ObjectQueue;
import lombok.AllArgsConstructor;
import lombok.Data;

public class TapeTest {
  @Data
  @AllArgsConstructor
  class Human {
    String name;
    int age;
  }

  public class GsonConverter implements FileObjectQueue.Converter<Human> {
    private final Gson gson = new Gson();

    @Override
    public Human from(byte[] bytes) {
      Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
      return gson.fromJson(reader, Human.class);
    }

    @Override
    public void toStream(Human object, OutputStream bytes) throws IOException {
      Writer writer = new OutputStreamWriter(bytes);
      gson.toJson(object, writer);
      writer.close();
    }
  }

  @Test
  public void testTape() {
    try {
      String quePath = "que";
      String queFilename = "test.que";
      File queFile = new File(quePath + File.separatorChar + queFilename);
      if (queFile.exists()) {
        queFile.delete();
      }

      ObjectQueue<Human> que = new FileObjectQueue<>(queFile, new GsonConverter());
      que.add(new Human("hasoo", 39));
      que.add(new Human("kim", 20));
      Assertions.assertEquals(2, que.size());

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public Path getFilePath(String fileDir, String filename) {
    File dir = new File(fileDir);
    if (!dir.exists())
      dir.mkdirs();

    return Paths.get(fileDir + File.separatorChar + filename);
  }
}
