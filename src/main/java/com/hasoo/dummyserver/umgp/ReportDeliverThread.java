package com.hasoo.dummyserver.umgp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportDeliverThread extends Thread {
  private UmgpWorker umgpWorker;
  private boolean isLoop = true;

  public ReportDeliverThread(UmgpWorker umgpWorker) {
    this.umgpWorker = umgpWorker;
  }

  @Override
  public void run() {
    while (this.isLoop) {
      umgpWorker.deliver();
    }
    log.debug("exited");
  }

  public void exit() {
    this.isLoop = false;
  }
}
