package io.github.hasoo.umgp;

import io.github.hasoo.dto.ReportQue;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class MemDeliveryRepository implements DeliveryRepository {

    private final Map<String, Queue<ReportQue>> reportQues = new HashMap<>();

    private MemDeliveryRepository() {
    }

    private static class SingletonHelper {
        private static final MemDeliveryRepository INSTANCE = new MemDeliveryRepository();
  }

  public static MemDeliveryRepository getInstance() {
    return SingletonHelper.INSTANCE;
  }

  @Override
  public ReportQue pop(String username) {
    synchronized (this) {
      Queue<ReportQue> ques = reportQues.get(username);
      if (null == ques) {
        return null;
      }

      ReportQue que = ques.poll();
      if (null != que) {
        return que;
      }
    }
    return null;
  }

  @Override
  public void push(String username, ReportQue reportQue) {
    synchronized (this) {
      Queue<ReportQue> ques = reportQues.get(username);
      if (null == ques) {
        reportQues.put(username, new LinkedList<ReportQue>(Arrays.asList(reportQue)));
      } else {
        ques.add(reportQue);
      }
    }
  }
}
