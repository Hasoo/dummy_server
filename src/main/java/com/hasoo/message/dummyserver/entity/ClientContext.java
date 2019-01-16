package com.hasoo.message.dummyserver.entity;

import com.hasoo.message.dummyserver.umgp.Umgp;
import io.netty.channel.Channel;
import lombok.Data;

@Data
public class ClientContext {
  private String username;
  private Channel channel;
  private Umgp umgp;

  Umgp.HType headerType;
  boolean reportline;

  public ClientContext(Channel channel) {
    this.channel = channel;
    this.umgp = new Umgp();

    this.username = "";
    this.reportline = false;
  }
}
