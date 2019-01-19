package com.hasoo.message.dummyserver.umgp;

import com.hasoo.message.dummyserver.entity.ReportQue;

public interface DeliveryRepository {
  public ReportQue get(String username);
}
