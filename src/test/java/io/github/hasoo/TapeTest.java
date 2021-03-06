package io.github.hasoo;

import com.google.gson.Gson;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.ObjectQueue.Converter;
import com.squareup.tape2.QueueFile;
import io.github.hasoo.util.HUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

public class TapeTest {
  private final File file = HUtil.getFilePath("./que/test", "test.que").toFile();

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
  public void testFileBasedTape() {
    try (QueueFile queueFile = new QueueFile.Builder(this.file).build()) {
      ObjectQueue<Human> queue =
              ObjectQueue.create(queueFile, new HumanConverter<Human>(Human.class));
      String name1 = "hasoo", name2 = "kim";
      int age1 = 39, age2 = 20;
      queue.add(new Human(name1, age1));
      queue.add(new Human(name2, age2));

      Assertions.assertEquals(2, queue.size());

      Human human = queue.peek();
      queue.remove();
      Assertions.assertEquals(name1, human.getName());
      human = queue.peek();
      queue.remove();
      Assertions.assertEquals(name2, human.getName());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testByteTape() {
    try (QueueFile queueFile = new QueueFile.Builder(this.file).build()) {
      for (int i = 0; i < 100; i++) {
        queueFile.add(String.valueOf(i).getBytes());
        queueFile.remove();
      }
      Assertions.assertEquals(0, queueFile.size());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Data
  @AllArgsConstructor
  class Human {
    private String name;
    private int age;
  }

  class HumanConverter<T> implements Converter<T> {
    private final Gson gson = new Gson();

    private final Class<T> classOfT;

    public HumanConverter(Class<T> classOfT) {
      this.classOfT = classOfT;
    }

    @Override
    public T from(byte[] source) throws IOException {
      Reader reader = new InputStreamReader(new ByteArrayInputStream(source));
      return gson.fromJson(reader, this.classOfT);
    }

    @Override
    public void toStream(T value, OutputStream sink) throws IOException {
      Writer writer = new OutputStreamWriter(sink);
      gson.toJson(value, writer);
      writer.close();
    }
  }
}
