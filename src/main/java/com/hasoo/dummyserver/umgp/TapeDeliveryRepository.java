package com.hasoo.dummyserver.umgp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import com.google.gson.Gson;
import com.hasoo.dummyserver.dto.ReportQue;
import com.hasoo.dummyserver.util.HUtil;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.ObjectQueue.Converter;
import com.squareup.tape2.QueueFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TapeDeliveryRepository implements DeliveryRepository {
  class DeliveryConverter<E> implements Converter<E> {
    private final Gson gson = new Gson();

    private Class<E> classOfT;

    public DeliveryConverter(Class<E> classOfT) {
      this.classOfT = classOfT;
    }

    @Override
    public E from(byte[] source) throws IOException {
      Reader reader = new InputStreamReader(new ByteArrayInputStream(source));
      return gson.fromJson(reader, this.classOfT);
    }

    @Override
    public void toStream(E value, OutputStream sink) throws IOException {
      Writer writer = new OutputStreamWriter(sink);
      gson.toJson(value, writer);
      writer.close();
    }
  }

  private TapeDeliveryRepository() {}

  private static class SingletonHelper {
    private static final TapeDeliveryRepository INSTANCE = new TapeDeliveryRepository();
  }

  public static TapeDeliveryRepository getInstance() {
    return SingletonHelper.INSTANCE;
  }

  @Override
  public ReportQue pop(String username) {
    synchronized (this) {
      File file = HUtil.getFilePath("./que/rslt", username).toFile();
      if (true != file.exists()) {
        return null;
      }

      try (QueueFile queueFile = new QueueFile.Builder(file).build()) {
        ObjectQueue<ReportQue> queue =
            ObjectQueue.create(queueFile, new DeliveryConverter<ReportQue>(ReportQue.class));
        ReportQue que = queue.peek();
        if (null != que) {
          queue.remove();
        }
        return que;
      } catch (IOException e) {
        log.error(HUtil.getStackTrace(e));
      }
    }
    return null;
  }

  @Override
  public void push(String username, ReportQue reportQue) {
    synchronized (this) {
      File file = HUtil.getFilePath("./que/rslt", username).toFile();
      try (QueueFile queueFile = new QueueFile.Builder(file).build()) {
        ObjectQueue<ReportQue> queue =
            ObjectQueue.create(queueFile, new DeliveryConverter<ReportQue>(ReportQue.class));
        queue.add(reportQue);
      } catch (IOException e) {
        log.error(HUtil.getStackTrace(e));
      }
    }
  }
}
