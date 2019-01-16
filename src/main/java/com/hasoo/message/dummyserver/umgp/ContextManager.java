package com.hasoo.message.dummyserver.umgp;

import java.util.HashMap;
import com.hasoo.message.dummyserver.entity.ClientContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

public class ContextManager {
  private HashMap<ChannelId, ClientContext> clientContexts = new HashMap<>();

  public synchronized void put(Channel channel) {
    clientContexts.put(channel.id(), new ClientContext(channel));
  }

  public synchronized void update(Channel channel, String username) {
    this.clientContexts.get(channel.id()).setUsername(username);
  }

  public synchronized ClientContext get(Channel channel) {
    return clientContexts.get(channel.id());
  }

  public synchronized void remove(Channel channel) {
    clientContexts.remove(channel.id());
  }
}
