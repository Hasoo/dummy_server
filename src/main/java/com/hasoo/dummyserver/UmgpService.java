package com.hasoo.dummyserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.hasoo.dummyserver.netty.UmgpServer;
import com.hasoo.dummyserver.umgp.ReportDeliverThread;
import com.hasoo.dummyserver.umgp.UmgpWorker;
import com.hasoo.dummyserver.util.HUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UmgpService {
  private static final String PROPERTIES_FILE = "./cfg/application.properties";
  private UmgpWorker umgpWorker;
  private ReportDeliverThread reportDeliverThread;
  private UmgpServer umgpServer;

  public void setup() throws FileNotFoundException, IOException {
    Properties prop = HUtil.getProperties(PROPERTIES_FILE);
    String umgpPort = prop.getProperty("umgp.server.port");

    log.info("umgp port:{}", umgpPort);

    umgpWorker = new UmgpWorker();
    umgpServer = new UmgpServer(umgpWorker);
    umgpServer.port(Integer.parseInt(umgpPort));
    reportDeliverThread = new ReportDeliverThread(umgpWorker);
  }

  public void run() {
    try {
      ExecutorService executor = Executors.newCachedThreadPool();
      executor.execute(reportDeliverThread);
      umgpServer.run();
    } catch (Exception e) {
      log.error(HUtil.getStackTrace(e));
    } finally {
      umgpServer.shutdown();
    }
  }

  public void shutdown() {
    reportDeliverThread.exit();
    umgpServer.shutdown();
  }

}
