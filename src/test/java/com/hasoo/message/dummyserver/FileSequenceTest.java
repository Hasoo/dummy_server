package com.hasoo.message.dummyserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.hasoo.message.dummyserver.util.FileSequence;
import com.hasoo.message.dummyserver.util.Util;

public class FileSequenceTest {

  @Test
  public void testSequence() {
    try {
      String path = "./seq", filename = "hasoo.seq";
      Path p = Util.getFilePath(path, filename);
      if (true == Files.exists(p)) {
        Files.delete(p);
      }

      // int maxValue = 1_000;
      int maxValue = 1_000_000;
      FileSequence fileSequence = new FileSequence(path, filename, maxValue);
      for (int i = 1; i <= maxValue; i++) {
        Assertions.assertEquals(String.valueOf(i), fileSequence.getSequence());
        // Assertions.assertEquals(String.valueOf(i), fileSequence.readSequence());
      }

      Assertions.assertEquals("1", fileSequence.getSequence());
      // Assertions.assertEquals("1", fileSequence.readSequence());

      fileSequence.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
