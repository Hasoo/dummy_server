package io.github.hasoo.umgp;

import com.google.gson.Gson;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.ObjectQueue.Converter;
import com.squareup.tape2.QueueFile;
import io.github.hasoo.dto.ReportQue;
import io.github.hasoo.util.HUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class TapeDeliveryRepository implements DeliveryRepository {
    class DeliveryConverter<E> implements Converter<E> {
        private final Gson gson = new Gson();

        private final Class<E> classOfT;

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
