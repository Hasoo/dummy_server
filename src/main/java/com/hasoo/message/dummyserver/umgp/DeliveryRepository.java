package com.hasoo.message.dummyserver.umgp;

import com.hasoo.message.dummyserver.dto.ReportQue;

public interface DeliveryRepository {
  public ReportQue pop(String username);

  public void push(String username, ReportQue reportQue);
}
