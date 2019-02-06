package com.hasoo.dummyserver.umgp;

import com.hasoo.dummyserver.dto.ClientContext;
import io.netty.channel.Channel;

public interface LineHandler {
  public void handle(Channel channel, ClientContext clientContext);
}
