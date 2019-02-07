package com.hasoo.dummyserver.umgp;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import com.hasoo.dummyserver.dto.ClientContext;
import com.hasoo.dummyserver.dto.ReportQue;
import com.hasoo.dummyserver.util.HUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UmgpWorker {
  private ContextManager contextManager = new ContextManager();
  private LineHandler sendLineHandler;
  private LineHandler reportLineHandler = new ReportLineHandler();
  private DeliveryRepository deliveryRepository;

  public UmgpWorker(DeliveryRepository deliveryRepository) {
    this.deliveryRepository = deliveryRepository;
    this.sendLineHandler = new SendLineHandler(deliveryRepository);
  }

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
        if (true != clientContext.isAuthenticated()) {
          if (Umgp.HType.CONNECT == clientContext.getHeaderType()) {
            log.debug("-> {} username:{} password:{} reportline:{} version:{}", Umgp.CONNECT,
                umgp.getId(), umgp.getPassword(), umgp.getReportline(), umgp.getVersion());
            if (umgp.getReportline().equals("Y")) {
              clientContext.setReportline(true);
            }
            authenticate(channel, clientContext);
          } else {
            throw new RuntimeException(
                String.format("authentication is required, invalid header:%s %s",
                    clientContext.getHeaderType(), who(channel)));
          }
        } else {
          if (clientContext.isReportline()) {
            reportLineHandler.handle(channel, clientContext);
          } else {
            sendLineHandler.handle(channel, clientContext);
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
    if (umgp.getId().equals("test") || umgp.getId().equals("skt") || umgp.getId().equals("kt")
        || umgp.getId().equals("lgt")) {
      sendConnectAck(channel, "100", "success");
      clientContext.setAuthenticated(true);
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

    if (0 != clientContext.getUsername().length()) {
      return String.format("%s:%d %s %s", clientAddress.getHostName(), clientAddress.getPort(),
          clientContext.getUsername(), clientContext.isReportline() ? "Y" : "N");
    }
    return String.format("%s:%d", clientAddress.getHostName(), clientAddress.getPort());
  }

  public boolean deliver() {
    try {
      ArrayList<ClientContext> ccs = contextManager.getReportLine();
      Iterator<ClientContext> it = ccs.iterator();
      while (it.hasNext()) {
        ClientContext cc = it.next();
        ReportQue que = deliveryRepository.pop(cc.getUsername());
        if (null != que) {
          sendReport(cc.getChannel(), que);
          return true;
        }
      }
    } catch (Exception ex) {
      log.error(HUtil.getStackTrace(ex));
    }
    return false;
  }

  private void sendReport(Channel channel, ReportQue que) {
    log.debug("<- {} key:{} code:{} data:{} date:{} net:{}", Umgp.REPORT, que.getKey(),
        que.getCode(), que.getData(), que.getDate(), que.getNet());
    StringBuilder packet = new StringBuilder();
    packet.append(Umgp.headerPart(Umgp.REPORT));
    packet.append(Umgp.dataPart(Umgp.KEY, que.getKey()));
    packet.append(Umgp.dataPart(Umgp.CODE, que.getCode()));
    packet.append(Umgp.dataPart(Umgp.DATA, que.getData()));
    packet.append(Umgp.dataPart(Umgp.DATE, que.getDate()));
    packet.append(Umgp.dataPart(Umgp.NET, que.getNet()));
    packet.append(Umgp.end());
    channel.writeAndFlush(packet.toString());
  }
}
