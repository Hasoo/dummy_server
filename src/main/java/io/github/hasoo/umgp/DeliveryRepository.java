package io.github.hasoo.umgp;

import io.github.hasoo.dto.ReportQue;

public interface DeliveryRepository {
    ReportQue pop(String username);

    void push(String username, ReportQue reportQue);
}
