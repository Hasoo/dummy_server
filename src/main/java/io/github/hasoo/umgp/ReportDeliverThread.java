package io.github.hasoo.umgp;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ReportDeliverThread extends Thread {
    private final UmgpWorker umgpWorker;
    private boolean isLoop = true;

    public ReportDeliverThread(UmgpWorker umgpWorker) {
        this.umgpWorker = umgpWorker;
    }

  @Override
  public void run() {
    while (this.isLoop) {
      if (true != umgpWorker.deliver()) {
        try {
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
      }
    }
    log.debug("exited");
  }

  public void exit() {
    this.isLoop = false;
  }
}
