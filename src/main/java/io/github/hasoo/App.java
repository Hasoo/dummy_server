package io.github.hasoo;

import io.github.hasoo.netty.UmgpServer;
import io.github.hasoo.umgp.MemDeliveryRepository;
import io.github.hasoo.umgp.ReportDeliverThread;
import io.github.hasoo.umgp.UmgpWorker;
import io.github.hasoo.util.HUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class App {
  public static void main(String[] args) throws IOException {

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
