package com.hasoo.message.dummyserver.umgp;

import java.net.InetSocketAddress;
import com.hasoo.message.dummyserver.entity.ClientContext;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UmgpWorker {
  ContextManager contextManager = new ContextManager();
  LineHandler sendLineHandler = new SendLineHandler();
  LineHandler reportLineHandler = new ReportLineHandler();

  public void connected(Channel channel) {
    contextManager.put(channel);
  }

  public void disconnected(Channel channel) {
    contextManager.remove(channel);
  }

  public void receive(Channel channel, String buf) {
    try {
      ClientContext clientContext = contextManager.get(channel);
      Umgp umgp = clientContext.getUmgp();

      if (true != umgp.isCompletedBegin()) {
        clientContext.setHeaderType(umgp.parseHeaderPart(buf));
      } else {
        umgp.parseDataPart(buf);
      }

      if (umgp.isCompletedEnd()) {
        if (Umgp.HType.CONNECT == clientContext.getHeaderType()) {
          if (umgp.getReportline().equals("Y")) {
            clientContext.setReportline(true);
          }
          authenticate(channel, clientContext);
        } else {
          if (clientContext.isReportline()) {
            sendLineHandler.handle(channel, clientContext);
          } else {
            reportLineHandler.handle(channel, clientContext);
          }
        }

        umgp.reset();
      }
    } catch (RuntimeException ex) {
      log.error(ex.getMessage());
      channel.close();
    }
  }

  public void authenticate(Channel channel, ClientContext clientContext) {
    Umgp umgp = clientContext.getUmgp();
    clientContext.setUsername(umgp.getId());
    log.debug("authentication username:{} password:{}", umgp.getId(), umgp.getPassword());
    if (umgp.getId().equals("test")) {
      sendConnectAck(channel, "100", "success");
    } else {
      sendConnectAck(channel, "200", "failure");
      channel.close();
    }
  }

  public void sendConnectAck(Channel channel, String code, String data) {
    StringBuilder packet = new StringBuilder();
    packet.append(Umgp.headerPart(Umgp.ACK));
    packet.append(Umgp.dataPart(Umgp.CODE, code));
    packet.append(Umgp.dataPart(Umgp.DATA, data));
    packet.append(Umgp.end());
    channel.writeAndFlush(packet.toString());
  }

  public String who(Channel channel) {
    ClientContext clientContext = contextManager.get(channel);
    InetSocketAddress clientAddress = (InetSocketAddress) channel.remoteAddress();

    String ret;
    if (0 != clientContext.getUsername().length()) {
      ret = String.format("%s:%d %s %s", clientAddress.getHostName(), clientAddress.getPort(),
          clientContext.getUsername(), clientContext.isReportline() ? "Y" : "N");
    } else {
      ret = String.format("%s:%d", clientAddress.getHostName(), clientAddress.getPort());
    }

    return ret;
  }
}
