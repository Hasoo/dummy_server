package com.hasoo.dummyserver.umgp;

import com.hasoo.dummyserver.dto.ReportQue;

public interface DeliveryRepository {
  public ReportQue pop(String username);

  public void push(String username, ReportQue reportQue);
}
