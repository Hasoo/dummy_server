package com.hasoo.message.dummyserver.umgp;

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
  }

  public void exit() {
    this.isLoop = false;
  }
}
