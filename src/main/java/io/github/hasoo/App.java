package com.hasoo.dummyserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import com.hasoo.dummyserver.netty.UmgpServer;
import com.hasoo.dummyserver.umgp.MemDeliveryRepository;
import com.hasoo.dummyserver.umgp.ReportDeliverThread;
import com.hasoo.dummyserver.umgp.UmgpWorker;
import com.hasoo.dummyserver.util.HUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
  public static void main(String[] args) throws FileNotFoundException, IOException {

    final String propFilename = "./cfg/application.properties";
    Properties prop = HUtil.getProperties(propFilename);

    String umgpPort = prop.getProperty("umgp.server.port");

    log.info("umgp port:{}", umgpPort);

    UmgpWorker umgpWorker = new UmgpWorker(MemDeliveryRepository.getInstance());

    ReportDeliverThread reportDeliverThread = new ReportDeliverThread(umgpWorker);
    reportDeliverThread.start();

    UmgpServer umgpServer = new UmgpServer(umgpWorker);
    umgpServer.port(Integer.parseInt(umgpPort));

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          log.debug("shutdown hooked");
          reportDeliverThread.exit();
          umgpServer.shutdown();
        } finally {
        }
        log.debug("shutdown finished");
      }
    });

    try {
      umgpServer.run();
    } catch (Exception e) {
      log.error(HUtil.getStackTrace(e));
    } finally {
      umgpServer.shutdown();
    }
  }
}
