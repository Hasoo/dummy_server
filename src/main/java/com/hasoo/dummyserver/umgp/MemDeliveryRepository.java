package com.hasoo.dummyserver.umgp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import com.hasoo.dummyserver.dto.ReportQue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemDeliveryRepository implements DeliveryRepository {

  private Map<String, Queue<ReportQue>> reportQues = new HashMap<>();

  private MemDeliveryRepository() {}

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
