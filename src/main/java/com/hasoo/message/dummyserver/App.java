package com.hasoo.message.dummyserver;

import com.hasoo.message.dummyserver.netty.UmgpServer;

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) {
    UmgpServer umgpServer = new UmgpServer(4000);
    try {
      umgpServer.run();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
