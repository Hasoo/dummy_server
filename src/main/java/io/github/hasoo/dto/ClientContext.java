package io.github.hasoo.dto;

import io.github.hasoo.umgp.Umgp;
import io.netty.channel.Channel;
import lombok.Data;

@Data
public class ClientContext {
  private String username;
  private Channel channel;
  private Umgp umgp;

  private Umgp.HType headerType;
  private boolean reportline;
  private boolean authenticated;

  public ClientContext(Channel channel) {
    this.channel = channel;
    this.umgp = new Umgp();

    this.username = "";
    this.reportline = false;
    this.authenticated = false;
  }
}
