package com.hasoo.message.dummyserver;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.hasoo.message.dummyserver.util.FileSequence;
import com.hasoo.message.dummyserver.util.Util;

public class FileSequenceTest {
  private File file = Util.getFilePath("./seq", "test.seq").toFile();

  @BeforeEach
  public void setUp() {
    if (this.file.exists()) {
      this.file.delete();
    }
  }

  @AfterEach
  public void setDown() {
    if (this.file.exists()) {
      this.file.delete();
    }
  }

  @Test
  public void testSequence() {
    try {
      /*
       * benchmark(1,000,000) opened:117,647 reopen:153,846 reopen lock:222,222 on linux
       */
      int maxValue = 1_000;
      // int maxValue = 1_000_000;
      FileSequence fileSequence = new FileSequence(this.file, maxValue);
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
