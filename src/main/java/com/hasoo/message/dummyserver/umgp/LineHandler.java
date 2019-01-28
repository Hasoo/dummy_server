package com.hasoo.message.dummyserver.umgp;

import com.hasoo.message.dummyserver.dto.ClientContext;
import io.netty.channel.Channel;

public interface LineHandler {
  public void handle(Channel channel, ClientContext clientContext);
}
