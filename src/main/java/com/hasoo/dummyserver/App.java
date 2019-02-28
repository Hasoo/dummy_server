package com.hasoo.dummyserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

  public static void main(String[] args) throws FileNotFoundException, IOException {

    UmgpService umgpService = new UmgpService();
    umgpService.setup();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          log.debug("shutdown hooked");
          umgpService.shutdown();
        } finally {
        }
        log.debug("shutdown finished");
      }
    });

    umgpService.run();
  }
}
