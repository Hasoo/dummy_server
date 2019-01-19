package com.hasoo.message.dummyserver.umgp;

import com.hasoo.message.dummyserver.entity.ClientContext;
import io.netty.channel.Channel;

public class SendLineHandler implements LineHandler {

  @Override
  public void handle(Channel channel, ClientContext clientContext) {
    Umgp.HType headerType = clientContext.getHeaderType();
    Umgp umgp = clientContext.getUmgp();

    if (Umgp.HType.SEND == headerType) {
      sendAck(channel, umgp.getKey(), umgp.getCode(), umgp.getData().toString());
    } else if (Umgp.HType.MMS == headerType) {
      sendAck(channel, umgp.getKey(), umgp.getCode(), umgp.getData().toString());
    } else if (Umgp.HType.PING == headerType) {
      sendPong(channel, umgp.getKey());
    }
  }

  public void sendAck(Channel channel, String key, String code, String data) {
    StringBuilder packet = new StringBuilder();
    packet.append(Umgp.headerPart(Umgp.ACK));
    packet.append(Umgp.dataPart(Umgp.KEY, key));
    packet.append(Umgp.dataPart(Umgp.CODE, code));
    packet.append(Umgp.dataPart(Umgp.DATA, data));
    packet.append(Umgp.end());
    channel.writeAndFlush(packet.toString());
  }

  public void sendPong(Channel channel, String key) {
    StringBuilder packet = new StringBuilder();
    packet.append(Umgp.headerPart(Umgp.PONG));
    packet.append(Umgp.dataPart(Umgp.KEY, key));
    packet.append(Umgp.end());
    channel.writeAndFlush(packet.toString());
  }
}
