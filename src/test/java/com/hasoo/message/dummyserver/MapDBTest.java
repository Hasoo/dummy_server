package com.hasoo.message.dummyserver;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;
import com.hasoo.dummyserver.util.HUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

public class MapDBTest implements Serializable {
  private static final long serialVersionUID = 677586736586749317L;

  @Data
  @AllArgsConstructor
  class Human {
    String name;
    int age;
  }

  class HumanSerializer implements Serializer<Human>, Serializable {
    private static final long serialVersionUID = 5114389836451200114L;

    @Override
    public void serialize(DataOutput2 out, Human value) throws IOException {
      out.writeUTF(value.getName());
      out.writeInt(value.getAge());
    }

    @Override
    public Human deserialize(DataInput2 input, int available) throws IOException {
      return new Human(input.readUTF(), input.readInt());
    }
  }

  private File file = HUtil.getFilePath("./db/test", "report.db").toFile();

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
  public void testHashMap() {

    DB db = DBMaker.fileDB(file).fileMmapEnable().make();

    Map<String, Human> map = db.hashMap("map").keySerializer(Serializer.STRING)
        .valueSerializer(new HumanSerializer()).createOrOpen();

    String aName = "aaa", bName = "bbb", cName = "ccc";
    int aAge = 1, bAge = 2, cAge = 3;
    map.put(aName, new Human(aName, aAge));
    map.put(bName, new Human(bName, bAge));
    map.put(cName, new Human(cName, cAge));

    Assertions.assertEquals(aAge, map.get(aName).getAge());
    Assertions.assertEquals(bAge, map.get(bName).getAge());
    Assertions.assertEquals(cAge, map.get(cName).getAge());

    db.close();
  }
}
