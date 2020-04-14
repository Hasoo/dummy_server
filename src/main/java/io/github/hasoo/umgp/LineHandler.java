package io.github.hasoo.umgp;

import io.github.hasoo.dto.ClientContext;
import io.netty.channel.Channel;

public interface LineHandler {
    void handle(Channel channel, ClientContext clientContext);
}
